package com.tj.boilerplate.coreapi.support

/**
 * 공통 응답 봉투 — 성공·실패가 같은 모양(code·message·data)이라 클라이언트가 한 가지 파싱만 알면 된다.
 * HTTP 상태로 성공 여부를 전하되, 분기 가능한 [code]를 본문에도 실어 상태코드 하나로는 부족한 구분을 남긴다.
 */
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?,
) {
    companion object {
        private const val SUCCESS_CODE = "0000"
        private const val SUCCESS_MESSAGE = "성공"

        fun <T> success(data: T?): ApiResponse<T> = ApiResponse(SUCCESS_CODE, SUCCESS_MESSAGE, data)

        // 실패 응답은 data가 없다 — Nothing으로 그 부재를 타입에 담고, code·message만 운반한다(ExceptionCode에는 비결합).
        fun error(
            code: String,
            message: String,
        ): ApiResponse<Nothing> = ApiResponse(code, message, null)
    }
}
