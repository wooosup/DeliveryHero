# DeliveryHero

--- 

## Project Overview
- 배달 플랫폼의 핵심 흐름(주문, 재고, 배달)을 구현하면서, 역할 기반 인가 경계, 상태 전이 정합성, 재고 동시성 문제를 테스트로 증명하는 것을 목표로 한 백엔드 포트폴리오 프로젝트입니다.
- [API 문서 바로가기](https://wooosup.github.io/DeliveryHero/)

## One-line Summary
- DeliveryHero는 주문-재고-배달 흐름에서 자주 깨지는 지점(권한 경계, 상태 전이, 동시성)을 도메인 규칙과 테스트로 고정한 Spring Boot 백엔드 프로젝트입니다.

## Technology Stack

- Java 17
- Spring Boot 3.5.6
- JPA
- Docker (MySQL)
- Swagger

## Key Problems Solved
- 역할 기반 인가 경계를 분리해 401/403를 명확히 구분했습니다.
- 주문 생성 요청을 `storeId`, `productId` 기반으로 바꿔 식별자와 표시값을 분리했습니다.
- 주문/배달의 상태 전이 규칙을 도메인에 모아 잘못된 요청 순서를 차단했습니다.
- 재고 차감 동시성을 MySQL(Testcontainers) 환경에서 검증해 oversell을 방지했습니다.

## Core Flow
주문 생성 → 재고 차감 → (사장) 주문 수락/거절 → (라이더) 배달 배차/시작/완료 → 주문 완료

## State Transition Summary

### Order
| 상태 | actor | 허용 동작 | 다음 상태 |
|---|---|---|---|
| `PENDING` | owner | accept | `ACCEPTED` |
| `PENDING` | owner | reject | `REJECTED` |
| `PENDING` | customer | cancel | `CANCELLED` |
| `ACCEPTED` | internal(from delivery complete) | complete | `COMPLETED` |

### Delivery
| 상태 | actor | 허용 동작 | 다음 상태 |
|---|---|---|---|
| `PENDING` | rider | assign | `ASSIGNED` |
| `ASSIGNED` | assigned rider | start | `PICKED_UP` |
| `PICKED_UP` | assigned rider | complete | `DELIVERED` |

## Testing Strategy
- Domain tests: 상태 전이/불변식을 도메인 객체에서 바로 검증해, 서비스가 바뀌어도 규칙이 깨지지 않도록 합니다.
- Service tests (fakes): use-case 흐름과 예외 케이스를 빠르게 검증해, 리팩터링 시 회귀를 줄입니다.
- Integration tests: 실제 Spring context에서 401/403 경계와 DB 락 동작을 검증해, “현실에서 깨지는 지점”을 막습니다.

근거가 되는 테스트 예시:
- 인가 통합 테스트: `AuthorizationIntegrationTest`
- 동시성 통합 테스트: `OrderConcurrencyIntegrationTest`

## Troubleshooting

### 1. 인증 주체가 섞여 있어 401과 403의 경계가 흐려지는 문제
- **문제**: 초기에는 하나의 로그인 주체 해석 방식에 customer, owner, rider가 함께 섞여 있어 컨트롤러 시그니처만 봐서는 누가 호출할 수 있는지 드러나지 않았고, 미인증 요청과 권한 없는 요청도 구분이 흐려질 수 있었습니다.
- **해결**: `@LoginCustomerId`, `@LoginOwnerId`, `@LoginRiderId`로 인증 주체를 역할별로 분리하고, 세션도 actor별 속성으로 정리했습니다. 또한 주문/배달 API에 actor별 시그니처를 반영하고 `AuthorizationIntegrationTest`로 401/403 시나리오를 검증했습니다.
- **결과**: 컨트롤러 메서드만 봐도 호출 주체를 바로 알 수 있게 되었고, 미인증은 `401`, 인증은 되었지만 잘못된 역할 접근은 `403`으로 명확하게 설명할 수 있게 되었습니다.

### 2. 시간 의존 비즈니스 로직으로 인해 테스트가 불안정해지는 문제
- **문제**: 주문 가능 시간, 가게 영업 시간, 배달 시작/완료 시각처럼 현재 시각에 따라 결과가 달라지는 로직이 많아 `LocalDateTime.now()`를 직접 사용하면 테스트 실행 시점마다 결과가 흔들릴 수 있었습니다.
- **해결**: `ClockHolder` 인터페이스로 현재 시간을 추상화하고, 운영 환경에서는 시스템 시간을 사용하되 테스트에서는 `TestClockHolder`로 고정 시간을 주입하도록 구성했습니다.
- **결과**: 영업 시간 검증, 배달 시작/완료 시간 기록 같은 시간 의존 로직을 예측 가능하게 테스트할 수 있게 되었고, 테스트의 재현성과 유지보수성이 좋아졌습니다.

### 3. 주문과 배달의 상태 전이 규칙이 분산되면 흐름 정합성이 깨지는 문제
- **문제**: 주문 수락, 주문 거절, 주문 취소, 배달 생성, 배달 시작, 배달 완료는 호출 순서가 중요한 상태 전이 로직인데, 이 규칙이 서비스나 컨트롤러에 흩어져 있으면 잘못된 요청 순서를 막기 어렵고 비즈니스 규칙을 설명하기도 어려웠습니다.
- **해결**: 상태 전이 책임을 도메인 객체에 모았습니다. `Order`는 `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED` 상태를 기준으로 고객 취소와 사장 거절을 구분하고, `Delivery`는 `ACCEPTED` 주문만 생성 가능하며 `PENDING -> ASSIGNED -> PICKED_UP -> DELIVERED` 흐름을 스스로 검증하도록 정리했습니다. 또한 외부 주문 완료 API를 제거하고, 배달 완료 시 주문 완료가 함께 이어지도록 서비스 흐름을 연결했습니다.
- **결과**: 잘못된 요청 순서는 도메인 단계에서 먼저 차단되고, 서비스 계층은 흐름 오케스트레이션에 집중할 수 있게 되었습니다. 덕분에 주문/배달 상태표를 코드와 같은 기준으로 설명할 수 있는 구조가 되었습니다.

### 4. 동시에 여러 주문이 들어올 때 재고 정합성이 깨질 수 있는 문제
- **문제**: 인기 상품에 주문이 동시에 몰리면 같은 재고를 여러 요청이 함께 읽고 차감해 oversell이 발생할 수 있습니다. 주문 생성과 재고 차감이 분리돼 있으면 이 문제는 더 쉽게 드러납니다.
- **해결**: 주문 생성 시 상품 조회 구간에 `PESSIMISTIC_WRITE`를 적용해 동일 상품 행에 대한 동시 접근을 직렬화했습니다. 그리고 MySQL Testcontainers 기반 `OrderConcurrencyIntegrationTest`를 추가해 재고가 1개인 상품에 주문 2건이 동시에 들어올 때 실제 DB에서도 락이 기대대로 동작하는지 검증했습니다.
- **결과**: 동시 주문 시 1건만 성공하고 1건은 실패하며 최종 재고는 0으로 유지되는 것을 테스트로 확인했습니다. 단순히 락을 선언하는 데 그치지 않고, 왜 이 전략을 선택했는지 실제 실행 결과로 설명할 수 있게 되었습니다.

## Future Improvements
- owner용 상품/가게 관리 API까지 `id-based` 계약으로 확장해 name-based 의존을 더 줄입니다.
- 상태 전이 표를 README에서 더 구체적으로 확장해, API 단위(엔드포인트)까지 연결해 설명합니다.
- 테스트 전략 섹션에 “각 테스트 계층이 막는 리스크”를 더 구체적으로 연결합니다.
- CI에서 Testcontainers 기반 테스트를 안정적으로 실행할 수 있는 환경을 구성합니다.

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
