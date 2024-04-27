package com.tj.boilerplate.lock

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.redis.lock")
data class RedisLockProperties(
    val host: String,
    val port: Int,
    val password: String,
)
