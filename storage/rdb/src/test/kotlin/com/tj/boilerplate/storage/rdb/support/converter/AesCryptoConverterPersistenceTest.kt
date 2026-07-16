package com.tj.boilerplate.storage.rdb.support.converter

import com.tj.boilerplate.modules.crypto.CryptoConfiguration
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

/**
 * 컨버터가 실제 JPA/Hibernate 경로에서 해석되는지 고정한다 — 단위 테스트([AesCryptoConverterTest])는 컨버터를 직접
 * 생성하므로 "Hibernate가 Encryptor를 주입해 준다"는 배선 가정을 전혀 검증하지 못한다. 이 테스트가 그 가정을 검증한다.
 * 함께 확인하는 것: DB에 실제로 암호문이 저장되는가(평문 유출 없음), 엔티티로는 평문이 왕복하는가.
 */
@DataJpaTest
@Import(CryptoConfiguration::class)
// 테스트 전용 base64 32B 키 — 소스에 커밋된 공개 값이므로 실사용 금지
@TestPropertySource(properties = ["crypto.secret-key=CF17hs/fBWRFlEtIBHmtwa2K7Pi3dh7Mg+dgFwmkqzI="])
class AesCryptoConverterPersistenceTest
    @Autowired
    constructor(
        private val entityManager: EntityManager,
    ) {
        @Test
        fun `엔티티는 평문으로 왕복하지만 DB에는 암호문이 저장된다`() {
            val secret = "010-1234-5678"

            val saved = SecretHolder(secret = secret)
            entityManager.persist(saved)
            entityManager.flush()
            entityManager.clear()

            // 엔티티 경로: 평문 왕복
            val found = entityManager.find(SecretHolder::class.java, saved.id)
            assertThat(found.secret).isEqualTo(secret)

            // DB 경로: 컨버터를 우회한 네이티브 조회로 실제 저장된 값이 암호문임을 확인
            val raw =
                entityManager
                    .createNativeQuery("select secret from secret_holder where id = ?1")
                    .setParameter(1, saved.id)
                    .singleResult as String
            assertThat(raw).isNotEqualTo(secret)
            assertThat(raw).doesNotContain("1234")
        }

        // 중첩 클래스라 기본 테이블명이 `..._test$secret_holder`가 된다 — 네이티브 조회를 위해 이름을 고정한다
        @Entity
        @Table(name = "secret_holder")
        class SecretHolder(
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            val id: Long? = null,
            @Convert(converter = AesCryptoConverter::class)
            val secret: String? = null,
        )
    }
