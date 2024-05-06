package com.tj.boilerplate.storage.redis

import com.fasterxml.jackson.module.kotlin.readValue
import com.tj.boilerplate.storage.redis.support.RedisStorageObjectMapperSupport
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisValueStorage(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getStringValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun set(
        key: String,
        value: String,
        lifeSpan: Duration?,
    ) {
        if (lifeSpan == null) {
            redisTemplate.opsForValue().set(key, value)
        } else {
            redisTemplate.opsForValue().set(key, value, lifeSpan)
        }
    }

    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}

inline fun <reified T> RedisValueStorage.get(key: String): T? {
    val value = getStringValue(key) ?: return null
    return RedisStorageObjectMapperSupport.objectMapper.readValue(value)
}

inline fun <reified T> RedisValueStorage.set(
    key: String,
    value: T,
    lifeSpan: Duration? = null,
) {
    set(key, RedisStorageObjectMapperSupport.objectMapper.writeValueAsString(value), lifeSpan)
}
