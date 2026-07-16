package com.tj.boilerplate.modules.crypto

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-256-GCM 암복호. 매 암호화마다 랜덤 IV를 생성해 `base64(IV || ciphertext+tag)`로 반환한다 —
 * 비결정론이라 같은 평문도 매번 다른 암호문이 된다(사전·상관 공격 방어).
 *
 * 뒤집으면: 암호문으로 동등 조회(`WHERE col = ?`)를 할 수 없다. 암호화 컬럼으로 검색해야 한다면
 * 별도 블라인드 인덱스(결정론적 HMAC 컬럼)가 필요하다 — 그 요구는 이 클래스가 풀지 않는다.
 *
 * 키는 생성자로 주입(설정/KMS)받되 32바이트(256비트)를 강제한다.
 */
class AesEncryptor(
    secretKeyBase64: String,
) : Encryptor {
    private val key: SecretKeySpec
    private val random = SecureRandom()

    init {
        val keyBytes = Base64.getDecoder().decode(secretKeyBase64)
        // AES 강도는 키 길이가 결정한다(GCM 태그·IV와 무관) — AES-256만 허용하도록 32바이트를 강제한다
        require(keyBytes.size == KEY_BYTES) { "AES-256 대칭키는 ${KEY_BYTES}바이트여야 합니다 (현재: ${keyBytes.size})" }
        key = SecretKeySpec(keyBytes, ALGORITHM)
    }

    override fun encrypt(plaintext: String): String {
        val iv = ByteArray(IV_BYTES).also(random::nextBytes)
        val cipher =
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
            }
        val sealed = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + sealed)
    }

    override fun decrypt(ciphertext: String): String {
        val decoded = Base64.getDecoder().decode(ciphertext)
        // 이 검사가 없으면 짧은 입력이 copyOfRange의 0-패딩을 타고 "12 > 5" 같은 산술 오류로 터진다 —
        // 기존 평문 컬럼에 @Convert를 뒤늦게 붙였을 때 실제로 만나는 경로라 원인을 말해주는 실패로 바꾼다
        require(decoded.size > IV_BYTES) { "유효한 암호문이 아닙니다 (최소 ${IV_BYTES + 1}바이트, 현재: ${decoded.size})" }
        val iv = decoded.copyOfRange(0, IV_BYTES)
        val sealed = decoded.copyOfRange(IV_BYTES, decoded.size)
        val cipher =
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, iv))
            }
        return String(cipher.doFinal(sealed), Charsets.UTF_8)
    }

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_BYTES = 32 // 256비트 = AES-256
        private const val IV_BYTES = 12 // 96비트 GCM nonce(권장)
        private const val TAG_BITS = 128 // GCM 인증 태그 길이(키 크기와 무관)
    }
}
