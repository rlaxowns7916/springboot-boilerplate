package com.tj.boilerplate.profile

import org.springframework.context.annotation.Profile

@Profile("live")
@Target(AnnotationTarget.CLASS)
annotation class LiveProfile
