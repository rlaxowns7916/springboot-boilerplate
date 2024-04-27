package com.tj.boilerplate.lock

import com.tj.boilerplate.profile.DevProfile
import com.tj.boilerplate.profile.LiveProfile
import org.springframework.boot.context.properties.ConfigurationProperties

@DevProfile
@LiveProfile
@ConfigurationProperties(prefix = "redis.lock")
data class RedisLockProperties(
    val host: String,
    val port: Int,
    val password: String?,
)
