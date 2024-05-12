package com.tj.boilerplate.modules.localcache

import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@SpringBootTest
class LocalCacheTest {
    @SpykBean
    lateinit var sut: TestRepository

    @Autowired
    lateinit var testService: TestService

    @Test
    fun `Cache가_적용되었다면_원본을_호출하지_않는다`() {
        // Cache Warming
        testService.execute(1L)
        verify(exactly = 1) { sut.findById(any()) }
        testService.execute(1L)
        verify(exactly = 1) { sut.findById(any()) }
    }
}

@Service
class TestService(
    val repo: TestRepository,
) {
    fun execute(id: Long): String {
        return repo.findById(id)
    }
}

@Repository
@CacheConfig(cacheNames = [CacheManagerNames.SAMPLE_CACHE])
class TestRepository {
    @Cacheable
    fun findById(id: Long): String {
        val result = "test:$id"
        println(result)

        return result
    }
}
