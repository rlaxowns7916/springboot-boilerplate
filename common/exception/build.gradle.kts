// common:exception — 전 계층 공용 예외 계약(leaf). 도메인/인프라 어디서든 던지고, 진입 모듈의 예외 핸들러가 응답으로 옮긴다.
// Spring 비의존인 순수 Kotlin 라이브러리라 의존성을 두지 않는다(ArchUnit의 `common 은 leaf` 규칙이 이를 강제한다).
