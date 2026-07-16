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

    implementation(project(":common:exception"))
    implementation(project(":domain"))
    implementation(project(":modules:pagination"))
    // opt-in 유틸 — 자동 배선은 없다. 비동기 MDC 전파는 앱이 executor에 MdcTaskDecorator를 붙여야 켜진다
    implementation(project(":modules:tracing"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // 로그 상관용 traceId — Micrometer Tracing/Brave. span export 아님(로그 MDC 상관 목적). 버전은 Spring dependency-management BOM 관리
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("openApiVersion")}")

    testImplementation("com.tngtech.archunit:archunit-junit5:${property("archunitVersion")}")
}
