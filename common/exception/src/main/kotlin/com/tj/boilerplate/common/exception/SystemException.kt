package com.tj.boilerplate.common.exception

/**
 * 호출자가 어쩔 수 없는 실패 — 인프라 오류, 외부 연동 실패, 설정 결함 등. 호출자에게 알릴 것이 없고 내부 구조만
 * 드러내므로 응답에는 [ExceptionCode.SYSTEM_ERROR]로 수렴시키고, 실제 [code]는 로그에만 남긴다.
 */
class SystemException(
    exceptionCode: ExceptionCode,
    cause: Throwable? = null,
) : BaseException(exceptionCode.code, exceptionCode.message, cause)
