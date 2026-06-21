package com.tj.boilerplate.coreapi.controller.system

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemController {
    @GetMapping("/health")
    fun healthCheck(): String = "pong"
}
