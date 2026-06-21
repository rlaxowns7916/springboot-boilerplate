package com.tj.boilerplate.modules.lock

import com.tj.boilerplate.common.profile.LocalProfile
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.embedded.RedisServer

@LocalProfile
@Configuration
class EmbeddedRedisLockConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val port = EmbeddedRedisExtensions.findAvailablePort()
    private lateinit var redisServer: RedisServer

    @Bean
    fun redissonClient(): RedissonClient {
        val redisHost = "redis://127.0.0.1:$port"
        val config = Config()
        config
            .useSingleServer()
            .apply {
                address = redisHost
                connectionPoolSize = 10
                connectionMinimumIdleSize = 1
            }

        return Redisson.create(config)
    }

    @PostConstruct
    fun postConstruct() {
        redisServer =
            if (EmbeddedRedisExtensions.isArmMac()) {
                EmbeddedRedisExtensions.createArmEmbeddedRedis("arm-embedded-redis-server", port)
            } else {
                RedisServer(port)
            }
        redisServer.start()
        logger.info("[EmbeddedRedis][Lock][Start][Complete] (port:$port)")
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
        logger.info("[EmbeddedRedis][Stop]")
    }
}
