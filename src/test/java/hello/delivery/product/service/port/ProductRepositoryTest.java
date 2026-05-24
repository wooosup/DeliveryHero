package hello.delivery.product.service.port;

import static hello.delivery.product.domain.ProductSellingStatus.STOP_SELLING;
import static hello.delivery.product.domain.ProductType.FOOD;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;

import hello.delivery.common.domain.Address;
import hello.delivery.common.domain.Money;
import hello.delivery.mock.FakeProductRepository;
import hello.delivery.product.domain.Product;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductRepositoryTest {

    private FakeProductRepository fakeProductRepository;

    @BeforeEach
    void setUp() {
        fakeProductRepository = new FakeProductRepository();
    }

    @Test
    @DisplayName("상품 타입으로 상품을 조회한다.")
    void findByProductType() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        Product product = Product.builder()
                .name("치킨")
                .productType(FOOD)
                .store(store)
                .price(Money.of(20000))
                .build();
        Product savedProduct = fakeProductRepository.save(product);

        // when
        List<Product> result = fakeProductRepository.findByProductType(store.getId(), FOOD);

        // then
        assertThat(result.get(0).getId()).isEqualTo(savedProduct.getId());
        assertThat(result.get(0).getName()).isEqualTo("치킨");
        assertThat(result.get(0).getPrice()).isEqualTo(Money.of(20000));
    }

    @Test
    @DisplayName("상품 판매 상태로 상품을 조회한다.")
    void findByProductSellingStatusIs() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        Product product = Product.builder()
                .name("치킨")
                .productType(FOOD)
                .store(store)
                .price(Money.of(20000))
                .productSellingStatus(STOP_SELLING)
                .build();
        Product savedProduct = fakeProductRepository.save(product);

        // when
        List<Product> result = fakeProductRepository.findByProductSellingStatusIs(store.getId(), STOP_SELLING);

        // then
        assertThat(result.get(0).getId()).isEqualTo(savedProduct.getId());
        assertThat(result.get(0).getName()).isEqualTo("치킨");
        assertThat(result.get(0).getPrice()).isEqualTo(Money.of(20000));
    }

    private static User buildOwner() {
        return User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address(Address.of("대구"))
                .role(OWNER)
                .build();
    }

    private static Store buildStore(User owner) {
        return Store.builder()
                .id(1L)
                .name("BBQ")
                .owner(owner)
                .build();
    }


}
