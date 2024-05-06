package com.tj.boilerplate.storage.redis.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisTemplateConfiguration {
    @Bean
    fun redisTemplate(connectionFactory: LettuceConnectionFactory): RedisTemplate<String, String> {
        return StringRedisTemplate(connectionFactory)
    }
}
