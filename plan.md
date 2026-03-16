# P2 Implementation Plan

## 1. 목적

이 문서는 [Task.md](/Users/sup/workspaces/DeliveryHero/Task.md)의 `P2` 범위를 구현하기 위한 실행 계획서다.
이번 `P2`의 목표는 기능 추가가 아니라, 저장소를 더 설명 가능하고 실행 가능하며 신뢰 가능한 상태로 만드는 것이다.

핵심 목표는 세 가지다.

1. DB 제약 조건과 인덱스 후보를 설명 가능한 형태로 정리한다.
2. 다른 개발자가 로컬에서 바로 실행할 수 있게 온보딩 문서를 보강한다.
3. 최소한의 `build/test` 자동화를 GitHub Actions에 추가한다.

이 문서는 이후 멀티 에이전트 구현이 쉽도록 아래를 명확히 한다.

- 이번 P2의 범위와 비범위
- 현재 저장소 기준선
- 워크스트림별 목표와 파일 소유권
- 병렬 가능 범위와 의존성
- 승인 게이트와 검증 명령

## 2. 현재 기준선

## 2.1 이미 끝난 것

- P0: 역할 기반 인가 경계, 상태 전이 규칙, 동시성 테스트 정리
- P1: 주문 생성 API의 ID 기반 계약 전환, README 톤 정리

## 2.2 현재 저장소에서 바로 보이는 사실

### 문서 / 실행 환경

- [`README.md`](/Users/sup/workspaces/DeliveryHero/README.md)에 핵심 흐름과 Troubleshooting은 정리돼 있다.
- 하지만 로컬 실행 절차, 설정 파일 준비 방법, Docker 필요 여부는 충분히 구체적이지 않다.
- 저장소에는 `docker-compose.yml`이 없다.
- `.gitignore`에는 `application-secret.properties`가 포함돼 있다.

### 애플리케이션 설정

- [`application.properties`](/Users/sup/workspaces/DeliveryHero/src/main/resources/application.properties)에 로컬 MySQL 주소가 직접 들어 있다.
- 현재 기본 설정은 아래와 같다.
  - DB URL: `localhost:3310/delivery`
  - username: `root`
  - profile: `local`
  - profile group: `local -> secret`
- 즉, 현재 구조는 `application-secret.properties`가 있다는 전제를 깔고 있다.

### CI 상태

- 현재 GitHub Actions는 [`.github/workflows/swagger-export.yml`](/Users/sup/workspaces/DeliveryHero/.github/workflows/swagger-export.yml) 하나만 있다.
- 이 워크플로우는 `main` 브랜치 push 기준이라, 현재 기본 브랜치 `master`와 맞지 않는다.
- `build/test`를 검증하는 PR용 워크플로우는 아직 없다.

### DB 제약 조건 / 조회 패턴

- 명시적 unique 제약은 현재 확인 기준으로 `users.username`, `riders.phone` 정도만 있다.
- `StoreEntity`, `ProductEntity`, `OrderEntity`에는 명시적 인덱스 정의가 없다.
- 현재 repository 기준으로 눈에 띄는 조회 패턴은 아래와 같다.
  - store: `findByStoreType`, `findByName`, `existsByName`, `findByStoresForOwner`
  - product: `findByStoreIdAndProductType`, `findByStoreIdAndProductSellingStatus`, `existsByStoreAndName`, `findByStore`, `findByIdWithLock`
  - order: `findOrdersByUserId`
  - delivery: `findByOrderId`
  - rider: `findByStatus`, `findByPhone`

## 3. P2 범위 결정

## 3.1 이번 P2에서 반드시 하는 것

### A. DB 설계 설명력 강화

- 현재 쿼리 패턴을 기준으로 인덱스 후보를 문서화한다.
- 현재 unique / non-unique 제약 조건을 표로 정리한다.
- “왜 아직 바로 스키마를 바꾸지 않았는지”까지 설명한다.

### B. 로컬 실행 온보딩 강화

- 예시 설정 파일을 추가한다.
- 개발용 MySQL 실행 방법을 제공한다.
- 테스트 실행 전제 조건까지 README에 적는다.

### C. 최소 CI 추가

- PR / push에서 `build` 또는 `test`가 자동 실행되게 한다.
- 기존 Swagger 워크플로우의 브랜치 기준도 현재 저장소와 맞춘다.

## 3.2 이번 P2에서 하지 않는 것

- 별도 승인 없는 실제 DB 마이그레이션
- 대규모 인덱스 추가나 unique constraint 추가
- Flyway / Liquibase 도입
- 배포 파이프라인 구축
- owner용 API나 추가 도메인 리팩터링

## 3.3 승인 게이트

이번 P2에서 가장 중요한 가드는 이것이다.

- 문서화, 예시 설정, Docker Compose, CI 추가는 바로 진행 가능
- 실제 DB 스키마 변경은 별도 승인 후 진행

이유:

- 인덱스 / 제약 조건 추가는 migration 성격을 가진다.
- 현재 프로젝트는 스키마 변경 이력을 관리하는 도구가 없다.
- 따라서 P2의 기본 결과물은 “설명 가능한 설계 문서 + 실행 가능 환경 + 자동 검증”으로 잡는다.

## 4. 설계 원칙

### 4.1 DB 원칙

- 먼저 “실제 조회 패턴”을 기준으로 후보를 정한다.
- 무결성 제약과 성능 인덱스를 분리해서 설명한다.
- name-based 로직이 아직 남아 있는 영역은 제약 추가 전 비즈니스 의미를 먼저 확인한다.

### 4.2 온보딩 원칙

- 현재 `local -> secret` 프로필 구조를 최대한 유지한다.
- 가장 작은 변화로 실행 경험을 개선한다.
- 실제 비밀번호나 민감 정보는 절대 예시 파일에 넣지 않는다.

### 4.3 CI 원칙

- 처음부터 복잡한 파이프라인을 만들지 않는다.
- 최소한 “PR에서 깨지는지”를 바로 알 수 있는 수준부터 만든다.
- 지금 있는 Swagger 자동화는 CI와 역할을 분리해 유지한다.

## 5. 멀티 에이전트 워크스트림

## 5.1 Workstream A. DB Review And Constraint Plan

### 목표

- 현재 DB 조회 패턴과 제약 조건을 정리한다.
- 인덱스 후보를 “근거 포함”으로 문서화한다.
- 실제 스키마 변경이 필요한 항목과 아직 보류할 항목을 분리한다.

### 추천 산출물

- `docs/db-review.md` 또는 동등한 별도 문서
- 필요 시 `README.md`의 짧은 링크/요약

### 소유 파일

- `docs/db-review.md` 신규 권장
- `src/main/java/hello/delivery/store/infrastructure/StoreJpaRepository.java`
- `src/main/java/hello/delivery/product/infrastructure/ProductJpaRepository.java`
- `src/main/java/hello/delivery/order/infrastructure/OrderJpaRepository.java`
- `src/main/java/hello/delivery/delivery/infrastructure/DeliveryJpaRepository.java`
- `src/main/java/hello/delivery/store/infrastructure/StoreEntity.java`
- `src/main/java/hello/delivery/product/infrastructure/ProductEntity.java`
- `src/main/java/hello/delivery/order/infrastructure/OrderEntity.java`
- `src/main/java/hello/delivery/user/infrastructure/UserEntity.java`
- `src/main/java/hello/delivery/rider/infrastructure/RiderEntity.java`

### 상세 작업

1. repository 메서드를 기준으로 주요 조회 패턴 목록을 만든다.
2. 각 조회 패턴이 어떤 컬럼 조합을 쓰는지 표로 정리한다.
3. 아래 후보를 우선 검토한다.
   - `orders.user_id`
   - `stores.owner_id`
   - `products.store_id + product_type`
   - `products.store_id + product_selling_status`
   - `deliveries.order_id` unique 후보
   - `riders.status`
4. 아래 제약 조건은 “후보”로만 검토하고 바로 추가하지 않는다.
   - `stores.name` unique 필요 여부
   - `products(store_id, name)` unique 필요 여부
5. “지금은 문서화만 하고 실제 스키마 변경은 보류”라는 판단 기준을 남긴다.

### 완료 기준

- 인터뷰에서 “왜 이 인덱스를 후보로 봤는지”를 설명할 수 있다.
- “왜 어떤 unique 제약은 아직 보류했는지”를 설명할 수 있다.

### 검증

- `rg -n "findBy|existsBy|@Query" src/main/java`
- 문서와 실제 repository/엔티티 정의가 어긋나지 않는지 수동 점검

## 5.2 Workstream B. Local Run And Onboarding

### 목표

- 처음 보는 개발자가 설정 파일과 DB 준비 방법을 바로 이해하게 만든다.
- README만 보고 로컬 실행과 테스트가 가능하도록 정리한다.

### 추천 산출물

- `application-secret.properties.example` 또는 동등한 예시 파일
- `docker-compose.yml`
- `README.md`의 `How to run` 확장

### 소유 파일

- `README.md`
- `docker-compose.yml` 신규 권장
- `application-secret.properties.example` 신규 권장
- 필요 시 `src/main/resources/application.properties`

### 상세 작업

1. 현재 설정 구조를 유지한 채 예시 파일 전략을 선택한다.
   - 권장: `application-secret.properties.example`
   - 이유: 현재 `local -> secret` 구조와 가장 잘 맞고 diff가 작다
2. 예시 파일에 아래 항목을 placeholder로 정리한다.
   - `spring.datasource.username`
   - `spring.datasource.password`
3. 개발용 MySQL Docker Compose를 추가한다.
   - 포트는 현재 애플리케이션 설정과 맞춰 `3310`
   - DB 이름은 `delivery`
4. README에 아래 절차를 명시한다.
   - 예시 파일 복사
   - Docker Compose 실행
   - `./gradlew bootRun`
   - `./gradlew test`
   - Swagger 접속 주소
5. Testcontainers 기반 테스트는 Docker가 켜져 있어야 한다는 점도 적는다.

### 완료 기준

- 저장소를 처음 받은 개발자가 추측 없이 로컬 실행 절차를 따라갈 수 있다.
- “왜 Docker가 필요한지”와 “어떤 파일을 준비해야 하는지”가 README에 적혀 있다.

### 검증

- `docker compose up -d`
- `./gradlew test`
- `./gradlew bootRun`

## 5.3 Workstream C. Minimal CI

### 목표

- PR에서 최소한의 자동 검증이 돌게 만든다.
- 현재 저장소와 맞지 않는 기존 워크플로우 트리거를 정리한다.

### 소유 파일

- `.github/workflows/ci.yml` 신규 권장
- `.github/workflows/swagger-export.yml`

### 상세 작업

1. 새 CI 워크플로우를 추가한다.
   - `pull_request` to `master`
   - `push` to `master`
2. Java 17과 Gradle 캐시를 사용한다.
3. 우선순위가 높은 검증 명령을 선택한다.
   - 권장 1안: `./gradlew test`
   - fallback: Testcontainers 이슈가 있으면 job 분리 또는 원인 분석 후 조정
4. 기존 Swagger 워크플로우의 브랜치를 `main`에서 `master`로 수정한다.
5. Swagger 문서 자동화는 “문서 생성용”, 새 CI는 “검증용”으로 역할을 나눈다.

### 완료 기준

- 새 PR에서 최소 1개의 자동 검증이 돈다.
- 기존 Swagger 자동화가 현재 기본 브랜치와 맞는다.

### 검증

- workflow YAML 문법 점검
- PR 생성 후 GitHub Actions 실행 결과 확인
- 가능하면 `./gradlew test`를 로컬에서 먼저 통과시킨 뒤 push

## 5.4 Workstream D. Parent Integration

### 목표

- 세 워크스트림 결과를 충돌 없이 합친다.
- Task / README / 문서 간 표현을 맞춘다.

### 소유 파일

- `Task.md`
- `README.md`
- 필요 시 `plan.md`

### 상세 작업

1. README와 별도 문서의 표현이 충돌하지 않게 정리
2. P2 구현이 끝난 항목만 `Task.md` 체크
3. 스키마 변경이 없었다면 그 이유를 PR 본문에 명시
4. 커밋 단위를 기능별로 분리

## 6. 병렬화 전략

## 6.1 바로 병렬 가능한 작업

- Workstream A: DB 리뷰 문서화
- Workstream B: 로컬 실행 가이드 + 예시 설정
- Workstream C: CI 워크플로우 추가

세 작업은 기본적으로 write set이 분리된다.

- A는 `docs/`와 DB 관련 코드 읽기 중심
- B는 `README.md`, `docker-compose.yml`, 예시 설정 파일
- C는 `.github/workflows/`

## 6.2 충돌 가능성이 있는 부분

- `README.md`는 Workstream B가 우선 소유
- Workstream A는 README를 직접 크게 수정하지 말고 별도 문서에 정리하는 것이 안전
- 부모 에이전트가 마지막에 README 링크/요약만 통합

## 6.3 권장 머지 순서

1. DB 리뷰 문서
2. 로컬 실행 가이드와 예시 설정
3. CI 워크플로우
4. 부모 통합 정리

## 7. 권장 커밋 단위

- `docs(db): 쿼리 패턴과 인덱스 후보 정리`
- `chore(local): 로컬 실행 예시 설정과 docker compose 추가`
- `docs(readme): 로컬 실행 가이드 보강`
- `ci(build): PR 빌드 테스트 워크플로우 추가`
- `ci(docs): swagger export 브랜치 트리거 수정`

## 8. 리스크와 대응

### 리스크 1. 인덱스/제약 조건 논의가 실제 스키마 변경으로 번질 수 있음

- 대응: P2 기본 산출물은 문서화로 제한
- 실제 schema change는 별도 승인 후 다음 단계로 분리

### 리스크 2. CI에서 Testcontainers가 예상보다 불안정할 수 있음

- 대응: 처음에는 `./gradlew test` 그대로 시도
- 실패 시 원인을 문서화하고 job 분리 또는 환경 조정 방향을 잡는다

### 리스크 3. 예시 설정 파일이 실제 비밀값처럼 보일 수 있음

- 대응: placeholder만 사용
- README에 “실제 값 입력 필요”를 명시

### 리스크 4. Docker Compose 포트가 로컬 환경과 충돌할 수 있음

- 대응: 현재 앱 기본값 `3310`을 유지하되, README에 변경 포인트를 함께 적는다

## 9. 구현 시작 전 체크리스트

- [ ] 실제 DB migration까지 할지 여부를 먼저 결정했는가
- [ ] P2는 문서화 중심인지, 스키마 변경까지 포함하는지 합의했는가
- [ ] 로컬 실행 예시 파일 형식을 `application-secret.properties.example`로 갈지 확정했는가
- [ ] CI에서 `./gradlew test`를 그대로 돌릴지 합의했는가

## 10. 한 줄 요약

> P2는 DeliveryHero를 “동작하는 프로젝트”에서 “설명 가능하고 실행 가능하며 자동 검증되는 프로젝트”로 끌어올리는 단계다.
