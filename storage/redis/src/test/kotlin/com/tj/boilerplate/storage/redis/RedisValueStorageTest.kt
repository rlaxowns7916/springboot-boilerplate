package com.tj.boilerplate.storage.redis

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.util.concurrent.TimeUnit

@SpringBootTest
class RedisValueStorageTest {
    @Autowired
    private lateinit var keyStorage: RedisKeyStorage

    @Autowired
    private lateinit var sut: RedisValueStorage

    @Test
    fun `Value를_가져올_때_Serialize하고_저장할_때_Deserialize할_수_있다`() {
        val key = "redis-value-serialize-deserialize-test"
        val expected = RedisStorageTestModel("key", "value")

        assertDoesNotThrow { sut.set(key, expected) }
        val actual = sut.get<RedisStorageTestModel>(key)

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Value저장시_Expire을_지정_할_수_있다`() {
        val key = "redis-value-expire-test"
        val ttl = Duration.ofSeconds(3)
        sut.set(key, "value", ttl)

        val expectedExpireExist = keyStorage.ttl(key, TimeUnit.SECONDS)
        assertThat(expectedExpireExist > 0).isTrue()

        Thread.sleep(ttl.toMillis())
        val expectedExpireNotExist = keyStorage.ttl(key, TimeUnit.SECONDS)
        assertThat(expectedExpireNotExist == -2L).isTrue()
    }
}
