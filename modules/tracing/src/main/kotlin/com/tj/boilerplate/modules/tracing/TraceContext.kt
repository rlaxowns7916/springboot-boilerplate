package com.tj.boilerplate.modules.tracing

import org.slf4j.MDC

/**
 * 로그 상관용 traceId를 MDC로 다루는 횡단 유틸. 현재 컨텍스트 traceId 추출([currentTraceId])과, 요청 스코프를 벗어난
 * 실행에 원본 traceId 재주입([withTraceId])을 제공한다. MDC 키는 이 유틸의 내부 관심사라 외부에 노출하지 않는다
 * (logback `%X{traceId}` 패턴과 일치하는 표준 키).
 */
object TraceContext {
    private const val MDC_KEY = "traceId"

    /**
     * 현재 스레드 MDC의 traceId — 없으면 null.
     *
     * traceId 존재는 관측 설정에 달린 문제라(샘플링 확률, 추적 스택 부재, 요청 스코프 밖 실행) 부재를 예외가 아닌 null로 다룬다.
     * 트레이싱 설정이 로직을 깨뜨리지 않게 하려는 것 — 호출자는 부재를 감수하거나(로그 필드), 자기 맥락에서 필요하면 직접 강제한다.
     */
    fun currentTraceId(): String? = MDC.get(MDC_KEY)

    /**
     * 저장된 원본 traceId를 MDC에 복원한 채로 [block]을 실행하고, 성공·예외 공통으로 정리한다(스레드 누수 방지).
     * 비동기 후속 처리처럼 요청 스코프를 벗어난 실행을 원 요청 traceId로 상관시키는 데 쓴다. traceId가 null이면 재주입 없이 실행한다.
     * 이미 스레드에 traceId가 있으면(예: HTTP 요청 스레드) 원복해 소실을 막는다.
     */
    fun <T> withTraceId(
        traceId: String?,
        block: () -> T,
    ): T {
        if (traceId == null) return block()
        val previous = MDC.get(MDC_KEY)
        MDC.put(MDC_KEY, traceId)
        return try {
            block()
        } finally {
            if (previous != null) MDC.put(MDC_KEY, previous) else MDC.remove(MDC_KEY)
        }
    }
}
