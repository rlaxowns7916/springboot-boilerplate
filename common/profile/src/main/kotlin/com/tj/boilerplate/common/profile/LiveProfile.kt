package com.tj.boilerplate.common.profile

import org.springframework.context.annotation.Profile

@Profile("live")
@Target(AnnotationTarget.CLASS)
annotation class LiveProfile
