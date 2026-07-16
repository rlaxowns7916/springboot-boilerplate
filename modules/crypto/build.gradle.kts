// modules:crypto — 대칭키 암호화 횡단 유틸(AES-256-GCM). 민감 정보를 저장 전 암호화하는 데 쓴다.
// 암복호 자체는 순수 JDK javax.crypto라 외부 SDK 비의존. 모듈이 자기 빈(@Bean Encryptor)·설정(CryptoProperties)을
// 소유하고, Spring은 compileOnly로만 참조한다(런타임 Spring은 진입 모듈이 제공).

dependencies {
    // @Configuration·@ConfigurationProperties 컴파일용 — 런타임 Spring은 진입 모듈(core-api)이 제공
    compileOnly("org.springframework.boot:spring-boot-starter")
    // 배선(조건부 빈) 검증에는 실제 컨텍스트가 필요하다
    testImplementation("org.springframework.boot:spring-boot-starter")
}
