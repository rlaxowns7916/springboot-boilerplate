package com.tj.boilerplate.storage.redis

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RedisListStorageTest {
    @Autowired
    private lateinit var sut: RedisListStorage

    @Test
    fun `Key에_해당하는_List의_모든_요소를_가지고_올_수_있댜`() {
        val key = "redis-list-get-test"
        val given =
            listOf(
                RedisStorageTestModel("key1", "value1"),
                RedisStorageTestModel("key2", "value2"),
                RedisStorageTestModel("key3", "value3"),
            )

        assertDoesNotThrow {
            given.forEach {
                sut.addLast(key, it)
            }
        }
        val actual = sut.get<RedisStorageTestModel>(key)
        assertThat(actual).containsExactly(given[0], given[1], given[2])
    }

    @Test
    fun `Key에_해당하는_List의_요소를_삭제할_수_있다`() {
        val key = "redis-list-remove-test"
        val given =
            listOf(
                RedisStorageTestModel("key4", "value4"),
                RedisStorageTestModel("key5", "value5"),
                RedisStorageTestModel("key6", "value6"),
            )

        assertDoesNotThrow { given.forEach { sut.addLast(key, it) } }
        val actual = sut.removeFirst<RedisStorageTestModel>(key)
        assertThat(actual).isEqualTo(given.first())
    }
}
