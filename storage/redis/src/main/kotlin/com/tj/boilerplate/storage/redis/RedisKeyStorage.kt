package com.tj.boilerplate.storage.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisKeyStorage(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun expire(
        key: String,
        timeout: Duration,
    ): Boolean = requireNotNull(redisTemplate.expire(key, timeout)) { "redis expire returned null" }

    fun ttl(
        key: String,
        timeUnit: TimeUnit = DEFAULT_TIME_UNIT,
    ): Long = redisTemplate.getExpire(key, timeUnit)

    companion object {
        private val DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS
    }
}
