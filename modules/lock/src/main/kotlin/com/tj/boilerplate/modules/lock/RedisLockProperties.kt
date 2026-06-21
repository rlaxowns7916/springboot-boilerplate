package com.tj.boilerplate.modules.lock

import com.tj.boilerplate.common.profile.DevProfile
import com.tj.boilerplate.common.profile.LiveProfile
import org.springframework.boot.context.properties.ConfigurationProperties

@DevProfile
@LiveProfile
@ConfigurationProperties(prefix = "redis.lock")
data class RedisLockProperties(
    val host: String,
    val port: Int,
    val password: String?,
)
