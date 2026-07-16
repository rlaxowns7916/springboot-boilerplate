package com.tj.boilerplate.modules.crypto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import java.util.Base64

/**
 * 배선 계약 검증 — 키가 있으면 켜지고, 없으면 조용히 꺼지되, 잘못된 키는 기동 시점에 실패한다.
 * "키를 잊으면 데모 키로 조용히 암호화되는" 사고를 구조적으로 막았는지가 이 테스트의 핵심이다.
 */
class CryptoConfigurationTest {
    private val contextRunner =
        ApplicationContextRunner()
            .withUserConfiguration(CryptoConfiguration::class.java)

    @Test
    fun `키가 설정되면 Encryptor 빈이 등록된다`() {
        contextRunner
            .withPropertyValues("crypto.secret-key=$TEST_KEY")
            .run { context ->
                assertThat(context).hasSingleBean(Encryptor::class.java)
                val encryptor = context.getBean(Encryptor::class.java)
                assertThat(encryptor.decrypt(encryptor.encrypt("roundtrip"))).isEqualTo("roundtrip")
            }
    }

    @Test
    fun `키가 없으면 모듈이 꺼진 채 기동한다 — 암호화를 쓰지 않는 앱을 막지 않는다`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).doesNotHaveBean(Encryptor::class.java)
        }
    }

    @Test
    fun `키가 비어 있으면 조용히 통과하지 않고 기동에 실패한다`() {
        contextRunner
            .withPropertyValues("crypto.secret-key=")
            .run { context ->
                assertThat(context).hasFailed()
            }
    }

    @Test
    fun `AES-256이 아닌 키는 기동 시점에 실패한다 — 약한 키로 조용히 암호화되지 않는다`() {
        contextRunner
            .withPropertyValues("crypto.secret-key=${Base64.getEncoder().encodeToString(ByteArray(16))}")
            .run { context ->
                assertThat(context).hasFailed()
            }
    }

    companion object {
        // 테스트 전용 base64 32B 키 — 소스에 커밋된 공개 값이므로 실사용 금지
        private const val TEST_KEY = "wdFgvMG5WJAuyqB3/I+bSgN6WwApD3XomFXuPkpdGuw="
    }
}
