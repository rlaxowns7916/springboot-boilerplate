package com.tj.boilerplate.modules.crypto

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Base64

class AesEncryptorTest {
    private val sut = AesEncryptor(TEST_KEY)

    @Test
    fun `암호화 후 복호화하면 원문으로 되돌아온다`() {
        val plaintext = "1234567890ABCDEF-민감정보"

        val encrypted = sut.encrypt(plaintext)

        assertThat(encrypted).isNotEqualTo(plaintext)
        assertThat(sut.decrypt(encrypted)).isEqualTo(plaintext)
    }

    @Test
    fun `같은 평문도 랜덤 IV로 매번 다른 암호문이 된다`() {
        val plaintext = "same-value"

        assertThat(sut.encrypt(plaintext)).isNotEqualTo(sut.encrypt(plaintext))
    }

    @Test
    fun `32바이트가 아닌 키는 AES-256이 아니므로 생성 시 거부한다`() {
        val key16 = Base64.getEncoder().encodeToString(ByteArray(16))

        assertThatThrownBy { AesEncryptor(key16) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `변조된 암호문은 GCM 인증 태그 검증에 걸려 복호에 실패한다`() {
        val encrypted = sut.encrypt("tamper-me")
        val decoded = Base64.getDecoder().decode(encrypted)
        decoded[decoded.size - 1] = (decoded[decoded.size - 1].toInt() xor 0x01).toByte()
        val tampered = Base64.getEncoder().encodeToString(decoded)

        assertThatThrownBy { sut.decrypt(tampered) }.isInstanceOf(javax.crypto.AEADBadTagException::class.java)
    }

    @Test
    fun `암호문이 아닌 짧은 입력은 원인을 말해주는 예외로 거부한다`() {
        // 기존 평문 컬럼에 @Convert를 뒤늦게 붙이면 만나는 경로 — 산술 오류가 아니라 설명이 나와야 한다
        val plaintextLike = Base64.getEncoder().encodeToString("short".toByteArray())

        assertThatThrownBy { sut.decrypt(plaintextLike) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("유효한 암호문이 아닙니다")
    }

    companion object {
        // 테스트 전용 base64 32B 키 — 소스에 커밋된 공개 값이므로 실사용 금지(운영 키는 환경변수/KMS로 주입한다)
        private const val TEST_KEY = "wdFgvMG5WJAuyqB3/I+bSgN6WwApD3XomFXuPkpdGuw="
    }
}
