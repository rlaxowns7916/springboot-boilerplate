package com.tj.boilerplate.storage.redis.configuration

import com.tj.boilerplate.profile.DevProfile
import com.tj.boilerplate.profile.LiveProfile
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@DevProfile
@LiveProfile
@Configuration
class RedisStorageConfiguration(
    private val properties: RedisProperties,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(properties.host, properties.port)
    }
}
