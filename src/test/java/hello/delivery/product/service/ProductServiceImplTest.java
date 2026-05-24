package hello.delivery.product.service;

import static hello.delivery.product.domain.ProductSellingStatus.STOP_SELLING;
import static hello.delivery.product.domain.ProductType.BEVERAGE;
import static hello.delivery.product.domain.ProductType.DESSERT;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.domain.Address;
import hello.delivery.common.domain.Money;
import hello.delivery.common.exception.ProductException;
import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.common.exception.StoreException;
import hello.delivery.mock.FakeFinder;
import hello.delivery.mock.FakeProductRepository;
import hello.delivery.product.service.port.in.ProductService;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductCreate;
import hello.delivery.product.domain.ProductType;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductServiceImplTest {

    private ProductService productService;
    private FakeFinder fakeFinder;

    private static final int ICED_AMERICANO_PRICE = 4000;
    private static final int CHEESE_CAKE_PRICE = 5500;
    private static final String STORE_NAME = "투썸";
    private static final String ICED_AMERICANO = "아아";
    private static final String CHEESE_CAKE = "치즈케이크";

    private User owner;
    private Store store;

    @BeforeEach
    void setUp() {
        FakeProductRepository fakeProductRepository = new FakeProductRepository();
        fakeFinder = new FakeFinder();
        productService = new ProductServiceImpl(fakeProductRepository, fakeFinder);

        owner = buildOwner();
        store = buildStore(owner);
    }

    @Test
    @DisplayName("가게에 상품을 등록할 수 있다.")
    void create() {
        // given
        ProductCreate productCreate = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);

        // when
        Product product = productService.create(owner.getId(), productCreate);

        // then
        assertThat(product.getName()).isEqualTo(ICED_AMERICANO);
        assertThat(product.getPrice()).isEqualTo(Money.of(ICED_AMERICANO_PRICE));
        assertThat(product.getProductType()).isEqualTo(BEVERAGE);
        assertThat(product.getStore().getName()).isEqualTo(STORE_NAME);
        assertThat(product.getStore().getOwner()).isEqualTo(owner);
    }

    @Test
    @DisplayName("가게에 여러 상품을 등록할 수 있다.")
    void creates() {
        // given
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreate(CHEESE_CAKE, DESSERT, CHEESE_CAKE_PRICE);

        // when
        List<Product> products = productService.creates(owner.getId(), List.of(product1, product2));

        // then
        assertThat(products).hasSize(2);

        assertThat(products.get(0).getName()).isEqualTo(ICED_AMERICANO);
        assertThat(products.get(0).getPrice()).isEqualTo(Money.of(ICED_AMERICANO_PRICE));
        assertThat(products.get(0).getProductType()).isEqualTo(BEVERAGE);
        assertThat(products.get(0).getStore().getName()).isEqualTo(STORE_NAME);

        assertThat(products.get(1).getName()).isEqualTo(CHEESE_CAKE);
        assertThat(products.get(1).getPrice()).isEqualTo(Money.of(CHEESE_CAKE_PRICE));
        assertThat(products.get(1).getProductType()).isEqualTo(DESSERT);
        assertThat(products.get(1).getStore().getName()).isEqualTo(STORE_NAME);
    }

    @Test
    @DisplayName("가게 주인이 아니면 예외를 던진다.")
    void validateCreate() {
        // given
        User anotherOwner = buildAnotherOwner();
        ProductCreate productCreate = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);

        // expect
        assertThatThrownBy(() -> productService.create(anotherOwner.getId(), productCreate))
                .isInstanceOf(StoreException.class)
                .hasMessageContaining("가게 소유자만 접근할 수 있습니다.");
    }

    @Test
    @DisplayName("모든 상품이 같은 가게가 아니면 예외를 던진다.")
    void validateCreates() {
        // given
        Store anotherStore = buildAnotherStore();
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreateWithStoreName(anotherStore.getName(), CHEESE_CAKE, DESSERT,
                CHEESE_CAKE_PRICE);

        // expect
        assertThatThrownBy(() -> productService.creates(owner.getId(), List.of(product1, product2)))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("모든 상품은 동일한 매장에 속해야 합니다.");
    }

    @Test
    @DisplayName("한 번에 등록하는 상품 목록에 중복 상품명이 있으면 예외를 던진다.")
    void validateDuplicateProductNamesInSameRequest() {
        // given
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreate(ICED_AMERICANO, DESSERT, CHEESE_CAKE_PRICE);

        // expect
        assertThatThrownBy(() -> productService.creates(owner.getId(), List.of(product1, product2)))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("이미 존재하는 상품입니다.");
    }

    @Test
    @DisplayName("상품의 판매 상태를 변경할 수 있다.")
    void changeSellingStatus() {
        // given
        ProductCreate productCreate = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        Product product = productService.create(owner.getId(), productCreate);
        fakeFinder.addProduct(product);

        // when
        Product result = productService.changeSellingStatus(product.getId(), owner.getId(), STOP_SELLING);

        // then
        assertThat(result.getProductSellingStatus()).isEqualTo(STOP_SELLING);
    }

    @Test
    @DisplayName("모든 상품을 조회할 수 있다.")
    void findAll() {
        // given
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreate(CHEESE_CAKE, DESSERT, CHEESE_CAKE_PRICE);
        productService.creates(owner.getId(), List.of(product1, product2));

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo(ICED_AMERICANO);
        assertThat(products.get(1).getName()).isEqualTo(CHEESE_CAKE);
    }

    @Test
    @DisplayName("상품 타입으로 상품을 조회할 수 있다.")
    void findByType() {
        // given
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreate(CHEESE_CAKE, DESSERT, CHEESE_CAKE_PRICE);
        productService.creates(owner.getId(), List.of(product1, product2));

        // when
        List<Product> result = productService.findByType(store.getId(), DESSERT);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(CHEESE_CAKE);
        assertThat(result.get(0).getStore().getName()).isEqualTo(STORE_NAME);
    }

    @Test
    @DisplayName("판매중인 상품을 조회할 수 있다.")
    void findBySelling() {
        // given
        ProductCreate product1 = createProductCreate(ICED_AMERICANO, BEVERAGE, ICED_AMERICANO_PRICE);
        ProductCreate product2 = createProductCreate(CHEESE_CAKE, DESSERT, CHEESE_CAKE_PRICE);
        List<Product> products = productService.creates(owner.getId(), List.of(product1, product2));

        fakeFinder.addProduct(products.get(0));
        fakeFinder.addProduct(products.get(1));

        productService.changeSellingStatus(products.get(0).getId(), owner.getId(), STOP_SELLING);

        // when
        List<Product> result = productService.findBySelling(store.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(CHEESE_CAKE);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 판매 상태를 변경하려고 하면 예외가 발생한다.")
    void notFoundProduct() {
        // given
        long productId = 999L;

        // expect
        assertThatThrownBy(() -> productService.changeSellingStatus(productId, owner.getId(), STOP_SELLING))
                .isInstanceOf(ProductNotFound.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }

    private ProductCreate createProductCreate(String name, ProductType type, int price) {
        return createProductCreateWithStoreName(STORE_NAME, name, type, price);
    }

    private ProductCreate createProductCreateWithStoreName(String storeName, String name, ProductType type, int price) {
        return ProductCreate.builder()
                .storeName(storeName)
                .type(type)
                .name(name)
                .price(price)
                .build();
    }

    private User buildOwner() {
        User owner = User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address(Address.of("대구"))
                .role(OWNER)
                .build();
        fakeFinder.addUser(owner);
        return owner;
    }

    private User buildAnotherOwner() {
        User anotherOwner = User.builder()
                .id(2L)
                .name("저녁뭐먹지")
                .username("흠..")
                .password("41215153")
                .address(Address.of("서울"))
                .role(OWNER)
                .build();
        fakeFinder.addUser(anotherOwner);
        return anotherOwner;
    }

    private Store buildStore(User owner) {
        Store store = Store.builder()
                .id(1L)
                .name(STORE_NAME)
                .owner(owner)
                .build();
        fakeFinder.addStore(store);
        return store;
    }

    private Store buildAnotherStore() {
        Store anotherStore = Store.builder()
                .id(2L)
                .name("스타벅스")
                .owner(owner)
                .build();
        fakeFinder.addStore(anotherStore);
        return anotherStore;
    }
}
