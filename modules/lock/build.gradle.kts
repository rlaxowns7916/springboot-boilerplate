dependencies {
    implementation(project(":common:profile"))
    implementation("it.ozimov:embedded-redis:${property("embeddedRedisVersion")}")
    implementation("org.redisson:redisson-spring-boot-starter:${property("redissonVersion")}")
}
