package com.tj.boilerplate.storage.rdb.support.converter

import com.tj.boilerplate.modules.crypto.AesEncryptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AesCryptoConverterTest {
    // 테스트 전용 base64 32B 키 — 소스에 커밋된 공개 값이므로 실사용 금지
    private val sut = AesCryptoConverter(AesEncryptor("wdFgvMG5WJAuyqB3/I+bSgN6WwApD3XomFXuPkpdGuw="))

    @Test
    fun `저장 시 암호화하고 조회 시 복호해 원문으로 왕복한다`() {
        val plaintext = "민감정보-value"

        val encrypted = sut.convertToDatabaseColumn(plaintext)

        assertThat(encrypted).isNotEqualTo(plaintext)
        assertThat(sut.convertToEntityAttribute(encrypted)).isEqualTo(plaintext)
    }

    @Test
    fun `null은 암복호 없이 null로 통과한다`() {
        assertThat(sut.convertToDatabaseColumn(null)).isNull()
        assertThat(sut.convertToEntityAttribute(null)).isNull()
    }
}
