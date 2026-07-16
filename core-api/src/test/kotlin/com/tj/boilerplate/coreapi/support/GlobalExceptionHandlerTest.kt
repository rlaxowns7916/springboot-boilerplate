package com.tj.boilerplate.coreapi.support

import com.tj.boilerplate.common.exception.BusinessException
import com.tj.boilerplate.common.exception.ExceptionCode
import com.tj.boilerplate.common.exception.SystemException
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * 예외 → 응답 변환 계약.
 *
 * 두 가지를 고정한다: (1) SystemException·미처리 예외가 내부 code를 새지 않고 9999로 수렴한다,
 * (2) MVC 표준 4xx(405·415·본문 파싱/검증 실패)가 catch-all에 삼켜져 500으로 둔갑하지 않는다.
 * (2)는 `@ExceptionHandler(Exception)`이 DefaultHandlerExceptionResolver보다 먼저 평가되는 데서 오는 회귀라
 * 핸들러 구조를 바꿀 때 가장 먼저 깨진다.
 */
class GlobalExceptionHandlerTest {
    private val mockMvc: MockMvc =
        MockMvcBuilders
            .standaloneSetup(ThrowingController())
            .setControllerAdvice(GlobalExceptionHandler())
            .build()

    @Test
    fun `BusinessException은 400과 함께 code·message를 그대로 전한다`() {
        mockMvc
            .perform(post("/business"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.code))
            .andExpect(jsonPath("$.message").value(ExceptionCode.INVALID_REQUEST.message))
            // 실패 응답의 data는 직렬화되되 항상 null이다(봉투 모양은 성공·실패가 동일)
            .andExpect(jsonPath("$.data").value(nullValue()))
    }

    @Test
    fun `SystemException은 500으로 내려가되 내부 code를 노출하지 않고 9999로 수렴한다`() {
        mockMvc
            .perform(post("/system"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value(ExceptionCode.SYSTEM_ERROR.code))
            .andExpect(jsonPath("$.message").value(ExceptionCode.SYSTEM_ERROR.message))
    }

    @Test
    fun `미처리 예외는 원인 메시지를 노출하지 않고 9999로 수렴한다`() {
        mockMvc
            .perform(post("/unexpected"))
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.code").value(ExceptionCode.SYSTEM_ERROR.code))
            .andExpect(jsonPath("$.message").value(ExceptionCode.SYSTEM_ERROR.message))
    }

    @Test
    fun `본문이 깨진 요청은 9001로 응답한다`() {
        mockMvc
            .perform(
                post("/body")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ this is not json"),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.code))
    }

    @Test
    fun `본문 검증 실패는 500이 아니라 400 9001로 응답한다`() {
        mockMvc
            .perform(
                post("/body")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":""}"""),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value(ExceptionCode.INVALID_REQUEST.code))
    }

    @Test
    fun `지원하지 않는 메서드는 catch-all에 삼켜지지 않고 405를 유지한다`() {
        mockMvc
            .perform(get("/business"))
            .andExpect(status().isMethodNotAllowed)
            .andExpect(jsonPath("$.code").value(ExceptionCode.METHOD_NOT_ALLOWED.code))
    }

    @Test
    fun `지원하지 않는 미디어 타입은 catch-all에 삼켜지지 않고 415를 유지한다`() {
        mockMvc
            .perform(
                post("/body")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("plain"),
            ).andExpect(status().isUnsupportedMediaType)
            .andExpect(jsonPath("$.code").value(ExceptionCode.UNSUPPORTED_MEDIA_TYPE.code))
    }

    data class Payload(
        @field:NotBlank
        val name: String,
    )

    @RestController
    class ThrowingController {
        @PostMapping("/business")
        fun business(): Nothing = throw BusinessException(ExceptionCode.INVALID_REQUEST)

        // SYSTEM_ERROR가 아닌 코드를 던져도 응답은 9999로 수렴해야 한다
        @PostMapping("/system")
        fun system(): Nothing = throw SystemException(ExceptionCode.INVALID_REQUEST)

        @PostMapping("/unexpected")
        fun unexpected(): Nothing = throw IllegalStateException("내부 구현 세부사항이 담긴 메시지")

        @PostMapping("/body")
        fun body(
            @Valid @RequestBody payload: Payload,
        ): String = payload.name
    }
}
