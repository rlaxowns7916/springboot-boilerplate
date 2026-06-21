dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("it.ozimov:embedded-redis:${project.property("embeddedRedisVersion")}")

    implementation(project(":common:profile"))
}
