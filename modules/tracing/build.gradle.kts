// modules:tracing — 관측성(traceId) 횡단 유틸. 현재 컨텍스트 traceId 추출(currentTraceId) + 요청 스코프를 벗어난
// 실행에 원본 traceId 재주입(withTraceId) + 비동기 워커에 MDC 전파(MdcTaskDecorator).
// 순수 slf4j MDC 기반이라 관측 스택(Brave)에 비의존, 계층 무관 재사용.
//
// opt-in 유틸이다 — 이 모듈은 아무것도 자동 배선하지 않는다. HTTP 요청 traceId는 core-api의 micrometer-tracing이
// 채우지만, 비동기 실행으로의 MDC 전파는 소비 앱이 자기 executor에 직접 붙여야 켜진다:
//   ThreadPoolTaskExecutor().apply { setTaskDecorator(MdcTaskDecorator()) }

dependencies {
    // TaskDecorator(spring-core)는 데코레이터 계약에만 필요 — 런타임 배선은 소비 모듈 몫
    compileOnly("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter")
}
