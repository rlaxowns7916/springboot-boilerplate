package com.tj.boilerplate.modules.localcache

import java.util.concurrent.TimeUnit

internal object DefaultCacheProperties {
    val DEFAULT_TTL: Long = 60 * 1000L
    val DEFAULT_TIME_UNIT: TimeUnit = TimeUnit.MILLISECONDS
    val DEFAULT_MAXIMUM_SIZE: Long = 100L
}
