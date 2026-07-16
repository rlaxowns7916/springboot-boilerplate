package com.tj.boilerplate.modules.tracing

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

/**
 * 비동기 워커 스레드에 제출 스레드의 MDC(traceId 등)를 전파해 워커 로그를 원 요청과 상관시키는 횡단 유틸.
 * 제출 시점 컨텍스트를 스냅샷해 실행 직전 워커에 심고, 실행 후 워커 MDC를 원복해 풀 스레드 재사용 시 traceId 누수를 막는다.
 * 관측 스택(Brave)에 비의존한 순수 slf4j MDC 복사 — 비동기 executor에 TaskDecorator로 장착해 쓴다.
 */
class MdcTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val submitterContext = MDC.getCopyOfContextMap()
        return Runnable {
            val previousContext = MDC.getCopyOfContextMap()
            setOrClear(submitterContext)
            try {
                runnable.run()
            } finally {
                setOrClear(previousContext)
            }
        }
    }

    private fun setOrClear(context: Map<String, String>?) {
        if (context != null) MDC.setContextMap(context) else MDC.clear()
    }
}
