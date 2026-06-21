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
    runtimeOnly(project(":modules:local-cache"))

    implementation(project(":domain"))
    implementation(project(":modules:pagination"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("openApiVersion")}")

    testImplementation("com.tngtech.archunit:archunit-junit5:${property("archunitVersion")}")
}
