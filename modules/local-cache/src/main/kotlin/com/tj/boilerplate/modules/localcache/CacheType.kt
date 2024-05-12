package com.tj.boilerplate.modules.localcache

import com.tj.boilerplate.modules.localcache.DefaultCacheProperties.DEFAULT_MAXIMUM_SIZE
import com.tj.boilerplate.modules.localcache.DefaultCacheProperties.DEFAULT_TIME_UNIT
import com.tj.boilerplate.modules.localcache.DefaultCacheProperties.DEFAULT_TTL
import java.util.concurrent.TimeUnit

object CacheManagerNames {
    const val SAMPLE_CACHE = "SAMPLE_CACHE"
}

internal enum class CacheType(
    val cacheManagerName: String,
    val ttl: Long,
    val timeUnit: TimeUnit,
    val maximumSize: Long,
) {
    SAMPLE_CACHE(CacheManagerNames.SAMPLE_CACHE, DEFAULT_TTL, DEFAULT_TIME_UNIT, DEFAULT_MAXIMUM_SIZE),
}
