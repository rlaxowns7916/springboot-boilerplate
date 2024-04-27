package com.tj.boilerplate.lock

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.CompletableFuture

@SpringBootTest
class RedisLockProcessorTest {
    @Autowired
    private lateinit var sut: RemoteLockProcessor

    @Test
    fun `Lock을_통해서_동시성문제를_해결_할_수_있댜`() {
        val taskSize = 500
        var counter = 0
        val taskExecutor =
            ThreadPoolTaskExecutor()
                .apply {
                    corePoolSize = 5
                    maxPoolSize = 10
                    queueCapacity = 500
                    initialize()
                }

        (1..taskSize).map {
            CompletableFuture.supplyAsync({
                sut.tryWithLock(
                    key = "RedisLockConcurrencyTest",
                ) { counter += 1 }
            }, taskExecutor)
        }.map { it.join() }

        assertThat(counter).isEqualTo(taskSize)
    }
}
