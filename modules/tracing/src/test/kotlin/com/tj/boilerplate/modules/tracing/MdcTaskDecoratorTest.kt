package com.tj.boilerplate.modules.tracing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.CompletableFuture

class MdcTaskDecoratorTest {
    // 단일 워커 풀 — 스레드 재사용 시 누수 검증을 위해 corePoolSize=maxPoolSize=1
    private val sut =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 1
            maxPoolSize = 1
            setThreadNamePrefix("test-worker-")
            setTaskDecorator(MdcTaskDecorator())
            initialize()
        }

    @AfterEach
    fun tearDown() {
        MDC.clear()
        sut.shutdown()
    }

    @Test
    fun `제출 스레드의 traceId가 워커 스레드로 전파된다`() {
        MDC.put("traceId", "trace-abc")

        val captured = CompletableFuture.supplyAsync({ MDC.get("traceId") }, sut).join()

        assertThat(captured).isEqualTo("trace-abc")
    }

    @Test
    fun `태스크 종료 후 워커 스레드 MDC에 traceId가 남지 않아 다음 태스크로 누수되지 않는다`() {
        MDC.put("traceId", "trace-xyz")
        CompletableFuture.supplyAsync({ MDC.get("traceId") }, sut).join()
        MDC.clear()

        // 같은(재사용) 워커 스레드에 traceId 없이 제출 — 이전 traceId가 남아있으면 누수
        val leaked = CompletableFuture.supplyAsync({ MDC.get("traceId") }, sut).join()

        assertThat(leaked).isNull()
    }
}
