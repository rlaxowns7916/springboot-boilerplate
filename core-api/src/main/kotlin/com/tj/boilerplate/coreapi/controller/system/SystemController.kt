package com.tj.boilerplate.coreapi.controller.system

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "시스템 API", description = "헬스 체크 등 시스템 엔드포인트")
@RestController
class SystemController {
    @Operation(summary = "헬스 체크", description = "서버 상태를 확인한다")
    @GetMapping("/health")
    fun healthCheck(): String = "pong"
}
