package com.tj.boilerplate.modules.localcache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun caffeineCaches(): List<CaffeineCache> =
        CacheType.entries.map { cache ->
            CaffeineCache(
                cache.cacheManagerName,
                Caffeine
                    .newBuilder()
                    .expireAfterWrite(cache.ttl, cache.timeUnit)
                    .maximumSize(cache.maximumSize)
                    .build(),
            )
        }

    @Bean
    fun cacheManager(caffeineCaches: List<CaffeineCache>): CacheManager = SimpleCacheManager().apply { setCaches(caffeineCaches) }
}
