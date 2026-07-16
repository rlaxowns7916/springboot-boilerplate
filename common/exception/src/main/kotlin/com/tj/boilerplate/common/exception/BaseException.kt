package com.tj.boilerplate.common.exception

/**
 * 애플리케이션 예외의 뿌리. 사람이 읽는 [message]와 별개로 기계가 분기하는 [code]를 운반한다 —
 * 응답 본문·로그·클라이언트 분기가 메시지 문자열 매칭에 기대지 않게 하는 것이 목적이다.
 */
abstract class BaseException(
    val code: String,
    override val message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
