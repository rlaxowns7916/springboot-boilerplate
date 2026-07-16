package com.tj.boilerplate.common.exception

/**
 * 예외 코드 목록.
 *
 * 코드 대역 규약: 1xxx~8xxx 도메인(서비스가 자기 대역을 골라 쓴다) / 9xxx 시스템·인프라(공통).
 * 아래 9xxx는 어떤 서비스에나 필요한 공통 대역이라 boilerplate가 미리 소유한다 — 도메인 코드는 서비스가 추가한다.
 *
 * "찾을 수 없음"·"이미 존재함" 같은 코드를 여기 미리 두지 않는 이유: 무엇을 못 찾았는지가 메시지의 핵심이라
 * 도메인 없이 쓰면 빈 껍데기가 된다. 도메인 대역에서 대상을 명시해 정의한다(예: `USER_NOT_FOUND("1001", "사용자를 찾을 수 없습니다")`).
 */
enum class ExceptionCode(
    val code: String,
    val message: String,
) {
    INVALID_REQUEST("9001", "요청 형식이 올바르지 않습니다"),
    RESOURCE_NOT_FOUND("9002", "요청한 리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED("9003", "지원하지 않는 요청 메서드입니다"),
    UNSUPPORTED_MEDIA_TYPE("9004", "지원하지 않는 미디어 타입입니다"),
    SYSTEM_ERROR("9999", "시스템 오류가 발생했습니다"),
}
