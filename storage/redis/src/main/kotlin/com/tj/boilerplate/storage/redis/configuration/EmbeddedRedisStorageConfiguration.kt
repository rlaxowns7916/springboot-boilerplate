package com.tj.boilerplate.storage.redis.configuration

import com.tj.boilerplate.common.profile.LocalProfile
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import redis.embedded.RedisServer

@LocalProfile
@Configuration
class EmbeddedRedisStorageConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val port = EmbeddedRedisExtensions.findAvailablePort()
    private lateinit var redisServer: RedisServer

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        logger.info("[EmbeddedRedis][Storage][Connection] (host: 127.0.0.1, port:$port)")
        return LettuceConnectionFactory("127.0.0.1", port)
    }

    @PostConstruct
    fun postConstruct() {
        redisServer =
            if (EmbeddedRedisExtensions.isArmMac()) {
                EmbeddedRedisExtensions.createEmbeddedRedis("arm-embedded-redis-server", port)
            } else {
                RedisServer(port)
            }
        redisServer.start()
        logger.info("[EmbeddedRedis][Storage][Start][Complete] (port:$port)")
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
        logger.info("[EmbeddedRedis][Stop]")
    }
}
