tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

dependencies {
    runtimeOnly(project(":storage:rdb"))
    runtimeOnly(project(":storage:redis"))
    runtimeOnly(project(":modules:lock"))

    implementation(project(":domain"))
    implementation(project(":modules:pagination"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
