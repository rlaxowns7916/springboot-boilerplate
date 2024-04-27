package com.tj.boilerplate.lock

import com.tj.boilerplate.profile.LocalProfile
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.file.Files

@LocalProfile
@Configuration
class EmbeddedRedisLockConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val port = findAvailablePort()
    private lateinit var redisServer: RedisServer

    @Bean
    fun redissonClient(): RedissonClient {
        val redisHost = "redis://127.0.0.1:$port"
        val config = Config()
        config.useSingleServer()
            .apply {
                address = redisHost
                connectionPoolSize = 10
                connectionMinimumIdleSize = 1
            }

        return Redisson.create(config)
    }

    @PostConstruct
    fun postConstruct() {
        val port = findAvailablePort()
        if (isArmMac()) {
            redisServer =
                RedisServer(
                    extractResourceAsFile("arm-embedded-redis-server"),
                    port,
                )
        } else {
            redisServer = RedisServer(port)
        }
        redisServer.start()
        logger.info("[EmbeddedRedis][Start][Complete] (port:$port)")
    }

    @PreDestroy
    fun preDestroy() {
        if (redisServer.isActive) {
            redisServer.stop()
            logger.info("[EmbeddedRedis][Stop]")
        }
    }

    private fun isArmMac(): Boolean {
        return System.getProperty("os.arch") == "aarch64" && System.getProperty("os.name") == "Mac OS X"
    }

    fun extractResourceAsFile(resourceName: String): File {
        val resource = ClassPathResource(resourceName)
        require(resource.exists()) { "Resource not found: $resourceName" }

        val tempFile: File =
            Files.createTempFile("resource-", ".tmp").toFile()
                .apply {
                    setExecutable(true)
                    deleteOnExit()
                }
        resource.inputStream.use { inputStream ->
            FileCopyUtils.copy(inputStream, FileOutputStream(tempFile))
        }
        return tempFile
    }

    private fun findAvailablePort(): Int {
        for (port in 10000..65535) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }

        throw IllegalArgumentException("Not Found Available port: 10000 ~ 65535")
    }

    private fun executeGrepProcessCommand(port: Int): Process {
        val command = String.format("netstat -nat | grep LISTEN|grep %d", port)
        val shell = arrayOf("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }

    private fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()

        BufferedReader(InputStreamReader(process.inputStream)).use { input ->
            while (input.readLine().also { line = it } != null) {
                pidInfo.append(line)
            }
        }

        return pidInfo.toString().isNotEmpty()
    }
}
