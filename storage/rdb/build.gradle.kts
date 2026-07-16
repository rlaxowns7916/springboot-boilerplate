dependencies {
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    implementation(project(":modules:pagination"))
    // 컬럼 암복호(@Convert) — 키가 설정된 앱에서만 켜지는 opt-in 배선
    implementation(project(":modules:crypto"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
