package com.tj.boilerplate.storage.rdb.support.converter

import com.tj.boilerplate.modules.crypto.Encryptor
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 암호화 대상 String 컬럼을 JPA 경계에서 투명하게 암복호한다 — 저장 시 암호화, 조회 시 복호.
 * `@Convert(converter = AesCryptoConverter::class)`로 지정한 어떤 String 컬럼에도 재사용한다(컬럼 무관).
 * 엔티티 속성은 평문, DB엔 암호문만 저장되고 null은 그대로 null.
 *
 * `autoApply = false`(기본) — 모든 String을 암호화하면 조회 불가 컬럼이 양산되므로 붙일 컬럼을 명시적으로 고르게 한다.
 *
 * [Encryptor] 주입 경로: Hibernate가 이 컨버터를 SpringBeanContainer로 생성하는데, JPA 호환 생성 규칙상
 * 컨테이너의 기존 빈을 조회(getBean)하는 게 아니라 매번 새 인스턴스를 만든다(createBean). 그래도 생성 과정에서
 * 생성자 오토와이어링이 돌아 [Encryptor] 빈이 주입된다 — 즉 이 클래스를 @Bean·@Component로 등록해 둘 필요는 없다.
 *
 * 따라서 이 컨버터를 쓰는 엔티티가 있는 앱은 [Encryptor] 빈이 반드시 있어야 한다(= `crypto.secret-key` 설정 필수).
 * 키 없이 이 컨버터를 붙인 엔티티를 두면 기동 시점에 실패한다 — 평문으로 조용히 저장되는 것보다 나은 실패다.
 */
@Converter
class AesCryptoConverter(
    private val encryptor: Encryptor,
) : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?): String? = attribute?.let(encryptor::encrypt)

    override fun convertToEntityAttribute(dbData: String?): String? = dbData?.let(encryptor::decrypt)
}
