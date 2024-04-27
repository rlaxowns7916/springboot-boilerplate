package com.tj.boilerplate.profile

import org.springframework.context.annotation.Profile

@Profile("dev")
@Target(AnnotationTarget.CLASS)
annotation class DevProfile
