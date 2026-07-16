package com.tj.boilerplate.modules.crypto

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 암호화 빈 배선 — 모듈이 자기 [Encryptor] 빈과 설정([CryptoProperties])을 스스로 소유한다.
 * 진입 모듈(com.tj.boilerplate 컴포넌트 스캔)이 이 @Configuration을 픽업한다.
 *
 * 키가 설정된 앱에서만 켜진다(opt-in) — boilerplate엔 암호화가 필요 없는 앱도 있고, 그런 앱까지
 * 키를 강제하면 모듈이 있다는 이유만으로 기동이 막힌다. 키를 넣는 행위가 곧 이 모듈을 켜는 행위다.
 */
@Configuration
@ConditionalOnProperty(prefix = "crypto", name = ["secret-key"])
@EnableConfigurationProperties(CryptoProperties::class)
class CryptoConfiguration(
    private val cryptoProperties: CryptoProperties,
) {
    @Bean
    fun encryptor(): Encryptor = AesEncryptor(cryptoProperties.secretKey)
}
