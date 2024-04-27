package com.tj.boilerplate.lock

import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RemoteLockProcessor(
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val redissonClient: RedissonClient,
) {
    fun <T> tryWithLock(
        key: String,
        waitTime: Long = DEFAULT_LOCK_WAIT_TIME,
        leaseTime: Long = DEFAULT_LOCK_LEASE_TIME,
        timeUnit: TimeUnit = DEFAULT_TIME_UNIT,
        block: () -> T?,
    ): T? {
        val lockKey = "$applicationName:$key"
        val lock = redissonClient.getLock(lockKey)

        try {
            val isLockAcquired = lock.tryLock(waitTime, leaseTime, timeUnit)
            if (!isLockAcquired) {
                throw RuntimeException("[Lock][Fail] (cause: Failed to Acquire Lock)")
            }
            return block()
        } finally {
            /**
             * @see RedissonLock.unlock()
             * 내부 구현에서 ThreadId 체킹 중이라, Lock획득여부 파악할 필요(X)
             */
            lock.unlock()
        }
    }

    companion object {
        private const val DEFAULT_LOCK_WAIT_TIME = 5000L
        private const val DEFAULT_LOCK_LEASE_TIME = 3000L
        private val DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS
    }
}
