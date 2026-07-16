package com.tj.boilerplate.modules.tracing

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC

class TraceContextTest {
    @AfterEach
    fun tearDown() {
        MDC.clear()
    }

    @Test
    fun `currentTraceId는 MDC의 traceId를 반환한다`() {
        MDC.put("traceId", "trace-current")

        assertThat(TraceContext.currentTraceId()).isEqualTo("trace-current")
    }

    @Test
    fun `currentTraceId는 MDC에 traceId가 없으면 null을 반환한다`() {
        assertThat(TraceContext.currentTraceId()).isNull()
    }

    @Test
    fun `withTraceId는 block 실행 동안 MDC에 traceId를 넣고 종료 후 제거한다`() {
        var during: String? = null

        TraceContext.withTraceId("trace-1") { during = MDC.get("traceId") }

        assertThat(during).isEqualTo("trace-1")
        assertThat(MDC.get("traceId")).isNull()
    }

    @Test
    fun `이미 스레드에 traceId가 있으면 block 종료 후 원래 traceId로 복원한다`() {
        MDC.put("traceId", "trace-original")
        var during: String? = null

        TraceContext.withTraceId("trace-injected") { during = MDC.get("traceId") }

        assertThat(during).isEqualTo("trace-injected")
        assertThat(MDC.get("traceId")).isEqualTo("trace-original")
    }

    @Test
    fun `traceId가 null이면 MDC를 건드리지 않고 block을 실행한다`() {
        var during: String? = "SENTINEL"

        TraceContext.withTraceId(null) { during = MDC.get("traceId") }

        assertThat(during).isNull()
    }

    @Test
    fun `block이 예외를 던져도 MDC가 정리된다`() {
        assertThatThrownBy {
            TraceContext.withTraceId("trace-2") { throw RuntimeException("boom") }
        }.isInstanceOf(RuntimeException::class.java)

        assertThat(MDC.get("traceId")).isNull()
    }
}
