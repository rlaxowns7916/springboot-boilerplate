dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core:${project.property("lettuceVersion")}")
    implementation("it.ozimov:embedded-redis:${project.property("embeddedRedisVersion")}")

    implementation(project(":common:profile"))
}
