package com.tj.boilerplate.common.profile

import org.springframework.context.annotation.Profile

@Profile("dev")
@Target(AnnotationTarget.CLASS)
annotation class DevProfile
