package com.tj.boilerplate.common.exception

/**
 * 호출자가 고칠 수 있는 실패 — 잘못된 요청, 규칙 위반, 상태 충돌 등. 예상된 흐름이라 스택트레이스 없이 warn 로그로 남고,
 * [code]·[message]가 그대로 응답에 실린다(클라이언트가 분기·표시할 수 있어야 하므로).
 */
class BusinessException(
    exceptionCode: ExceptionCode,
    cause: Throwable? = null,
) : BaseException(exceptionCode.code, exceptionCode.message, cause)
