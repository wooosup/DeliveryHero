package hello.delivery.product.domain;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;
import static hello.delivery.product.domain.ProductSellingStatus.SOLD_OUT;
import static hello.delivery.product.domain.ProductSellingStatus.STOP_SELLING;
import static hello.delivery.product.domain.ProductType.BEVERAGE;
import static hello.delivery.product.domain.ProductType.FOOD;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.ProductException;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void create() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName(store.getName())
                .name("치킨")
                .price(20000)
                .type(FOOD)
                .build();

        // when
        Product product = Product.of(productCreate, store, owner);

        // then
        assertThat(product.getName()).isEqualTo("치킨");
        assertThat(product.getPrice()).isEqualTo(20000);
        assertThat(product.getProductType()).isEqualTo(FOOD);
        assertThat(product.getProductSellingStatus()).isEqualTo(SELLING);
    }

    @Test
    @DisplayName("재고가 있는 상품을 생성할 수 있다.")
    void createProductWithStock() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName(store.getName())
                .name("콜라")
                .price(1500)
                .type(BEVERAGE)
                .stock(5)
                .build();

        // when
        Product product = Product.of(productCreate, store, owner);

        // then
        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(1500);
        assertThat(product.getProductType()).isEqualTo(BEVERAGE);
        assertThat(product.getProductSellingStatus()).isEqualTo(SELLING);
        assertThat(product.getStock().getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("재고가 0이면 품절 상태로 상품을 생성할 수 있다.")
    void checkProductOfSoldOut() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName(store.getName())
                .name("콜라")
                .price(1500)
                .type(BEVERAGE)
                .stock(0)
                .build();

        // when
        Product product = Product.of(productCreate, store, owner);

        // then
        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(1500);
        assertThat(product.getProductType()).isEqualTo(BEVERAGE);
        assertThat(product.getProductSellingStatus()).isEqualTo(SOLD_OUT);
        assertThat(product.getStock().getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("상품 가격이 1원 미만이면 예외를 던진다.")
    void validatePrice() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName(store.getName())
                .name("치킨")
                .price(0)
                .type(FOOD)
                .build();

        // expect
        assertThatThrownBy(() -> Product.of(productCreate, store, owner))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("상품 가격은 양수여야 합니다.");
    }

    @Test
    @DisplayName("가게 주인이 일치하지 않으면 예외를 던진다.")
    void invalidStoreOwner() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName("ㅋㅋ")
                .name("치킨")
                .price(20000)
                .type(FOOD)
                .build();

        // expect
        assertThatThrownBy(() -> Product.of(productCreate, store, owner))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("가게가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("판매 상태를 변경한다.")
    void changeSellingStatus() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        ProductCreate productCreate = ProductCreate.builder()
                .storeName(store.getName())
                .name("치킨")
                .price(20000)
                .type(FOOD)
                .build();
        Product product = Product.of(productCreate, store, owner);

        // when
        Product result = product.changeSellingStatus(STOP_SELLING);

        // then
        assertThat(result.getProductSellingStatus()).isEqualTo(STOP_SELLING);
    }

    private static User buildOwner() {
        return User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address("대구")
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