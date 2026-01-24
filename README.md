# DeliveryHero

--- 

## Project Overview
- 본 프로젝트는 배달 플랫폼의 비즈니스 흐름을 구현한 것입니다. 기술적인 측면에서는 도메인 주도 설계를 기반으로 비즈니스 로직을 중심에 두고, Spring Boot나 JPA와 같은 외부 기술에 도메인이 의존하지 않도록 헥사고날 아키텍처를 점진적으로 적용하는 것을 주된 목표로 삼았습니다.
- [API 문서 바로가기](https://wooosup.github.io/deliveryApp/)

## Technology stack

- Java 17
- Spring Boot 3.5.6
- JPA
- Docker (MySQL)
- Swagger

## Engineering Focus: Testability
- **추상화 기반 테스트**: `ClockHolder` 인터페이스를 도입하여 현재 시간 등 외부 환경에 의존적인 로직을 예측 가능하게 테스트함.
- **도메인 순수성**: 도메인 객체(`User`, `Product` 등)를 순수 Java 객체로 설계하여 프레임워크 기술(JPA 등)의 변화에 영향을 받지 않도록 보호함.

## Architecture Diagram
<div align="center">
    <img src="src/main/resources/static/architecture.png" width="700">
</div>

## ERD Diagram
<div align="center">
    <img src="src/main/resources/static/ERD.png" width="700">
</div>

## How to run

- build: ./gradlew build
- run: ./gradlew bootRun
- test: ./gradlew test
- swagger: http://localhost:8080/swagger-ui/index.html
