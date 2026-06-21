package com.tj.boilerplate.lock

import com.tj.boilerplate.profile.DevProfile
import com.tj.boilerplate.profile.LiveProfile
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@DevProfile
@LiveProfile
@Configuration
@EnableConfigurationProperties(value = [RedisLockProperties::class])
class RemoteRedisLockConfiguration(
    private val properties: RedisLockProperties,
) {
    @Bean
    fun redissonClient(): RedissonClient {
        val redisHost = "redis://${properties.host}:${properties.port}"
        val config = Config()
        config
            .useSingleServer()
            .apply {
                address = redisHost
                password = properties.password
                connectionPoolSize = 10
                connectionMinimumIdleSize = 1
            }

        return Redisson.create(config)
    }
}
