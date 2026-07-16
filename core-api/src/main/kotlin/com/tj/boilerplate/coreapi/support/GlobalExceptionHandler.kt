package com.tj.boilerplate.coreapi.support

import com.tj.boilerplate.common.exception.BusinessException
import com.tj.boilerplate.common.exception.ExceptionCode
import com.tj.boilerplate.common.exception.SystemException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * 예외를 응답으로 옮기는 단일 지점 — 컨트롤러가 try-catch 없이 도메인 흐름만 담게 한다.
 *
 * 노출 규칙: [BusinessException]은 code·message를 그대로 전하고(호출자가 고칠 수 있는 실패),
 * [SystemException]·미처리 예외는 내부 정보를 감춘 채 9999로 수렴시킨다.
 *
 * [ResponseEntityExceptionHandler]를 상속하는 이유: `@ExceptionHandler(Exception)`을 가진 @RestControllerAdvice는
 * Spring MVC의 DefaultHandlerExceptionResolver보다 먼저 평가된다. 상속 없이 catch-all만 두면 404·405·415 같은
 * MVC 표준 4xx까지 catch-all이 삼켜 500 + ERROR 스택트레이스로 둔갑한다. 부모가 그 표준 예외들을 먼저 처리하고,
 * 우리는 [handleExceptionInternal]에서 본문만 [ApiResponse] 봉투로 바꿔 상태코드를 보존한다.
 */
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn { "[GlobalExceptionHandler][handleBusiness] (code: ${e.code})" }
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(e.code, e.message))
    }

    /** `@Validated` 파라미터(@RequestParam·@PathVariable) 위반 — 본문 검증(MethodArgumentNotValidException)은 부모가 맡는다. */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn { "[GlobalExceptionHandler][handleConstraintViolation] (violations: ${e.constraintViolations.map { it.propertyPath }})" }
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(ExceptionCode.INVALID_REQUEST.code, ExceptionCode.INVALID_REQUEST.message))
    }

    // 내부 code는 응답에 노출하지 않는다 — SystemException은 일괄 9999로 수렴, 상세는 로그에만.
    @ExceptionHandler(SystemException::class)
    fun handleSystem(e: SystemException): ResponseEntity<ApiResponse<Nothing>> {
        log.error(e) { "[GlobalExceptionHandler][handleSystem] (code: ${e.code})" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ExceptionCode.SYSTEM_ERROR.code, ExceptionCode.SYSTEM_ERROR.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error(e) { "[GlobalExceptionHandler][handleUnexpected] (cause: ${e.message})" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ExceptionCode.SYSTEM_ERROR.code, ExceptionCode.SYSTEM_ERROR.message))
    }

    /**
     * MVC 표준 예외(404·405·415·본문 파싱 실패·본문 검증 실패 등)의 응답 본문을 [ApiResponse]로 통일한다.
     * 부모가 정한 상태코드는 그대로 두고 본문 표현만 바꾼다 — 클라이언트가 성공·실패에 같은 파싱을 쓰게 하려는 것.
     */
    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val exceptionCode = exceptionCodeOf(statusCode)
        if (statusCode.is5xxServerError) {
            log.error(ex) { "[GlobalExceptionHandler][handleExceptionInternal] (status: $statusCode)" }
        } else {
            log.warn { "[GlobalExceptionHandler][handleExceptionInternal] (status: $statusCode, cause: ${ex.message})" }
        }
        return ResponseEntity
            .status(statusCode)
            .headers(headers)
            .body(ApiResponse.error(exceptionCode.code, exceptionCode.message))
    }

    private fun exceptionCodeOf(statusCode: HttpStatusCode): ExceptionCode =
        when {
            statusCode.is5xxServerError -> ExceptionCode.SYSTEM_ERROR
            statusCode.isSameCodeAs(HttpStatus.NOT_FOUND) -> ExceptionCode.RESOURCE_NOT_FOUND
            statusCode.isSameCodeAs(HttpStatus.METHOD_NOT_ALLOWED) -> ExceptionCode.METHOD_NOT_ALLOWED
            statusCode.isSameCodeAs(HttpStatus.UNSUPPORTED_MEDIA_TYPE) -> ExceptionCode.UNSUPPORTED_MEDIA_TYPE
            else -> ExceptionCode.INVALID_REQUEST
        }
}
