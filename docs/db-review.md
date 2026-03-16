# DeliveryHero DB Review

## 목적

이 문서는 현재 DeliveryHero의 조회 패턴, 제약 조건, 인덱스 후보를 정리한 문서다.

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

