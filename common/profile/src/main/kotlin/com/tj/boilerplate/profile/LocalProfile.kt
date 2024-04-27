package com.tj.boilerplate.profile

import org.springframework.context.annotation.Profile

@Profile("local")
@Target(AnnotationTarget.CLASS)
annotation class LocalProfile
