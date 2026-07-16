package com.tj.boilerplate.modules.crypto

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 암호화 키 설정(crypto.secret-key) — AES-256-GCM 대칭키(base64 32바이트).
 *
 * 비밀값이라 이 모듈은 기본값을 제공하지 않는다. 커밋된 데모 키를 기본값으로 두면 환경변수 주입을 잊었을 때
 * 공개된 키로 조용히 암호화되어 암호화하지 않은 것과 다름없어진다 — 그 사고를 없애려고 기본값 자체를 두지 않는다.
 * 대신 키가 없으면 [CryptoConfiguration]이 아예 켜지지 않는다(모듈 미사용으로 간주).
 *
 * 주입: `CRYPTO_SECRET_KEY` 환경변수(relaxed binding) 또는 앱 yml의 `crypto.secret-key`. 실운영은 KMS로 공급한다.
 * 로컬 키 생성: `openssl rand -base64 32`
 */
@ConfigurationProperties("crypto")
data class CryptoProperties(
    val secretKey: String,
) {
    init {
        // 빈 문자열 주입(예: 미설정 환경변수 치환)으로 조건을 통과해 놓고 런타임에 깨지는 경로를 기동 시점에 끊는다
        require(secretKey.isNotBlank()) { "crypto.secret-key가 비어 있습니다 (CRYPTO_SECRET_KEY 환경변수로 주입합니다)" }
    }
}
