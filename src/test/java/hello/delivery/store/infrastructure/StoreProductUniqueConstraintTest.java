package hello.delivery.store.infrastructure;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;
import static hello.delivery.product.domain.ProductType.BEVERAGE;
import static hello.delivery.store.domain.StoreType.KOREAN_FOOD;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.domain.Address;
import hello.delivery.common.domain.Money;
import hello.delivery.product.infrastructure.ProductEntity;
import hello.delivery.product.infrastructure.ProductJpaRepository;
import hello.delivery.user.infrastructure.UserEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class StoreProductUniqueConstraintTest {

    @Autowired
    private StoreJpaRepository storeJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    @Test
    @DisplayName("가게명은 중복 저장할 수 없다.")
    void storeNameUniqueConstraint() {
        // given
        UserEntity owner = persistOwner("owner-1");
        storeJpaRepository.save(storeEntity(owner, "BBQ"));
        storeJpaRepository.flush();

        // when & then
        assertThatThrownBy(() -> {
            storeJpaRepository.save(storeEntity(owner, "BBQ"));
            storeJpaRepository.flush();
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("같은 가게에는 같은 상품명을 중복 저장할 수 없다.")
    void productNameUniqueConstraintInSameStore() {
        // given
        UserEntity owner = persistOwner("owner-2");
        StoreEntity store = storeJpaRepository.save(storeEntity(owner, "BBQ"));
        storeJpaRepository.flush();

        productJpaRepository.save(productEntity(owner, store, "콜라"));
        productJpaRepository.flush();

        // when & then
        assertThatThrownBy(() -> {
            productJpaRepository.save(productEntity(owner, store, "콜라"));
            productJpaRepository.flush();
        })
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("서로 다른 가게는 같은 상품명을 사용할 수 있다.")
    void productNameCanBeDuplicatedAcrossDifferentStores() {
        // given
        UserEntity owner = persistOwner("owner-3");
        StoreEntity store1 = storeJpaRepository.save(storeEntity(owner, "BBQ"));
        StoreEntity store2 = storeJpaRepository.save(storeEntity(owner, "교촌"));
        storeJpaRepository.flush();

        // when & then
        productJpaRepository.save(productEntity(owner, store1, "콜라"));
        productJpaRepository.save(productEntity(owner, store2, "콜라"));
        assertThatCode(productJpaRepository::flush).doesNotThrowAnyException();
    }

    private UserEntity persistOwner(String username) {
        return entityManager.persist(UserEntity.builder()
                .name("사장")
                .username(username)
                .password("password")
                .address(Address.of("대구"))
                .role(OWNER)
                .build());
    }

    private StoreEntity storeEntity(UserEntity owner, String name) {
        return StoreEntity.builder()
                .owner(owner)
                .name(name)
                .dailySales(0)
                .totalSales(0)
                .storeType(KOREAN_FOOD)
                .openDate(LocalDate.of(2026, 5, 25))
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(21, 0))
                .build();
    }

    private ProductEntity productEntity(UserEntity owner, StoreEntity store, String name) {
        return ProductEntity.builder()
                .owner(owner)
                .store(store)
                .name(name)
                .price(Money.of(1000))
                .productType(BEVERAGE)
                .productSellingStatus(SELLING)
                .build();
    }

}
