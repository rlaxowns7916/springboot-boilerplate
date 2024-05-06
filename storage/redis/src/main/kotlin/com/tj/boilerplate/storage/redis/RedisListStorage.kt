package com.tj.boilerplate.storage.redis

import com.fasterxml.jackson.module.kotlin.readValue
import com.tj.boilerplate.storage.redis.support.RedisStorageObjectMapperSupport
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisListStorage(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun range(key: String): List<String> {
        return redisTemplate.opsForList().range(key, 0, -1) ?: emptyList()
    }

    fun lPop(key: String): String? {
        return redisTemplate.opsForList().leftPop(key)
    }

    fun rPop(key: String): String? {
        return redisTemplate.opsForList().rightPop(key)
    }

    fun lPush(
        key: String,
        value: String,
    ) {
        redisTemplate.opsForList().leftPush(key, value)!!
    }

    fun rPush(
        key: String,
        value: String,
    ) {
        redisTemplate.opsForList().rightPush(key, value)!!
    }
}

inline fun <reified T> RedisListStorage.get(key: String): List<T> {
    val list = range(key)
    return list.map { RedisStorageObjectMapperSupport.objectMapper.readValue(it) }
}

inline fun <reified T> RedisListStorage.removeFirst(key: String): T? {
    val removedValue = lPop(key)
    return removedValue?.let { RedisStorageObjectMapperSupport.objectMapper.readValue(it) }
}

inline fun <reified T> RedisListStorage.removeLast(key: String): T? {
    val removedValue = rPop(key)
    return removedValue?.let { RedisStorageObjectMapperSupport.objectMapper.readValue(it) }
}

inline fun <reified T> RedisListStorage.addFirst(
    key: String,
    value: T,
) {
    lPush(key, RedisStorageObjectMapperSupport.objectMapper.writeValueAsString(value))
}

inline fun <reified T> RedisListStorage.addLast(
    key: String,
    value: T,
) {
    rPush(key, RedisStorageObjectMapperSupport.objectMapper.writeValueAsString(value))
}
