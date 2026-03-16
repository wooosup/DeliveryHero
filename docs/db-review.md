# DeliveryHero DB Review

## 목적

이 문서는 현재 DeliveryHero의 조회 패턴, 제약 조건, 인덱스 후보를 정리한 문서다.
이번 단계에서는 실제 스키마를 바꾸지 않고, 어떤 후보를 왜 검토하는지 설명하는 데 집중한다.

## 현재 명시적 제약 조건

| 대상 | 현재 상태 | 근거 |
|---|---|---|
| `users.id` | PK | JPA `@Id` |
| `users.username` | unique | `UserEntity.java` |
| `riders.id` | PK | JPA `@Id` |
| `riders.phone` | unique | `RiderEntity.java` |
| `stores.id` | PK | JPA `@Id` |
| `products.id` | PK | JPA `@Id` |
| `orders.id` | PK | JPA `@Id` |
| `deliveries.id` | PK | JPA `@Id` |
| `deliveries.order_id` | unique | `DeliveryEntity.order` 1:1 매핑 |

현재 `StoreEntity`, `ProductEntity`, `OrderEntity`, `DeliveryEntity`에는 명시적 인덱스 정의가 없다.

## 주요 조회 패턴

### Store

| 메서드 | 조건 | 후보 인덱스 |
|---|---|---|
| `findByStoreType` | `store_type` | `stores(store_type)` |
| `findByName` / `existsByName` | `name` | `stores(name)` 또는 unique 여부 검토 |
| `findByStoresForOwner` | `owner_id` | `stores(owner_id)` |

### Product

| 메서드 | 조건 | 후보 인덱스 |
|---|---|---|
| `findByStoreIdAndProductType` | `store_id`, `product_type` | `products(store_id, product_type)` |
| `findByStoreIdAndProductSellingStatus` | `store_id`, `product_selling_status` | `products(store_id, product_selling_status)` |
| `findByStore` | `store_id` | `products(store_id)` |
| `existsByStoreAndName` | `store_id`, `name` | `products(store_id, name)` |
| `findByIdWithLock` | `id` | PK로 충분 |

### Order / Delivery / Rider

| 메서드 | 조건 | 후보 인덱스 |
|---|---|---|
| `findOrdersByUserId` | `user_id` | `orders(user_id)` |
| `findByOrderId` | `order_id` | 이미 unique |
| `findByStatus` | `status` | `riders(status)` |
| `findByPhone` | `phone` | 이미 unique |

## 우선 검토할 인덱스 후보

### 1. `orders(user_id)`

- 이유: 고객 기준 주문 목록 조회는 기본적인 조회 패턴이다.
- 기대 효과: 주문 이력 조회 시 full scan 가능성을 줄일 수 있다.

### 2. `stores(owner_id)`

- 이유: 사장 기준 가게 조회가 이미 repository에 존재한다.
- 기대 효과: owner별 가게 목록 조회 비용을 줄일 수 있다.

### 3. `products(store_id, product_type)`

- 이유: 가게 내 메뉴 카테고리 조회가 이미 분리돼 있다.
- 기대 효과: 특정 가게의 특정 타입 메뉴 조회를 더 안정적으로 지원한다.

### 4. `products(store_id, product_selling_status)`

- 이유: 판매 중 / 판매 중지 상태별 조회가 이미 있다.
- 기대 효과: 메뉴 노출 상태 기반 조회 성능을 보강한다.

### 5. `riders(status)`

- 이유: 현재 라이더 가용 상태 조회가 repository API로 존재한다.
- 기대 효과: 배차 가능한 라이더 목록 조회 비용을 줄일 수 있다.

## 보류한 unique 제약 조건

### `stores.name`

- 바로 unique로 고정하지 않았다.
- 이유: 같은 이름의 가게를 허용할지 여부는 비즈니스 정책이 먼저 정리돼야 한다.
- 현재 상태에서는 검색용 인덱스 후보로만 보는 편이 안전하다.

### `products(store_id, name)`

- 바로 unique로 고정하지 않았다.
- 이유: 같은 가게 안에서 이름이 같은 메뉴를 허용하지 않을지 먼저 합의가 필요하다.
- 현재는 `existsByStoreAndName`가 있지만, name-based 로직이 완전히 제거된 상태는 아니다.

## 지금 스키마 변경을 바로 하지 않은 이유

- 현재 프로젝트는 Hibernate `ddl-auto=update` 기반이라 migration 이력이 없다.
- 인덱스 / 제약 조건 추가는 운영 데이터 정합성 점검과 롤백 전략이 필요하다.
- 따라서 이번 단계는 “실제 변경”보다 “후보와 근거를 정리하는 단계”로 두는 편이 안전하다.

## 다음 단계 제안

1. 실제 데이터 기준 중복 여부를 먼저 확인한다.
2. migration 도구(Flyway 또는 동등한 수단) 도입 여부를 결정한다.
3. 우선순위가 높은 인덱스부터 작은 PR로 추가한다.
4. 추가 후에는 관련 repository 조회와 통합 테스트를 다시 점검한다.
