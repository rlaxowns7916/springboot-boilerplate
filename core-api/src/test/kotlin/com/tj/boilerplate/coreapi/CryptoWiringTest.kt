package com.tj.boilerplate.coreapi

import com.tj.boilerplate.modules.crypto.Encryptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

/**
 * 실제 진입 모듈 컨텍스트에서 crypto 배선을 고정한다 — modules:crypto 는 core-api 에 runtimeOnly 로만 걸려 있어
 * 컴포넌트 스캔이 이를 집어가는지는 컨텍스트를 띄워야만 확인된다(모듈 자체 테스트는 설정 클래스를 직접 등록하므로 못 잡는다).
 */
class CryptoWiringTest {
    @SpringBootTest
    @TestPropertySource(properties = ["crypto.secret-key=CF17hs/fBWRFlEtIBHmtwa2K7Pi3dh7Mg+dgFwmkqzI="])
    class KeyConfigured
        @Autowired
        constructor(
            private val context: ApplicationContext,
        ) {
            @Test
            fun `키가 설정되면 스캔이 modules crypto 를 집어 Encryptor 빈이 등록된다`() {
                val encryptor = context.getBean(Encryptor::class.java)

                assertThat(encryptor.decrypt(encryptor.encrypt("wired"))).isEqualTo("wired")
            }
        }

    @SpringBootTest
    class KeyAbsent
        @Autowired
        constructor(
            private val context: ApplicationContext,
        ) {
            @Test
            fun `키가 없으면 Encryptor 없이 정상 기동한다 — 암호화를 쓰지 않는 앱을 막지 않는다`() {
                assertThat(context.getBeanNamesForType(Encryptor::class.java)).isEmpty()
            }
        }
}
