package hello.delivery.order.service;

import static hello.delivery.order.domain.OrderStatus.ACCEPTED;
import static hello.delivery.order.domain.OrderStatus.CANCELLED;
import static hello.delivery.order.domain.OrderStatus.COMPLETED;
import static hello.delivery.order.domain.OrderStatus.REJECTED;
import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.common.exception.OrderException;
import hello.delivery.common.exception.ProductException;
import hello.delivery.common.exception.StockException;
import hello.delivery.delivery.service.DeliveryServiceImpl;
import hello.delivery.mock.FakeDeliveryRepository;
import hello.delivery.mock.FakeFinder;
import hello.delivery.mock.FakeOrderRepository;
import hello.delivery.mock.FakeProductRepository;
import hello.delivery.mock.FakeRiderRepository;
import hello.delivery.mock.FakeStoreRepository;
import hello.delivery.mock.TestClockHolder;
import hello.delivery.order.controller.port.OrderService;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderCreate;
import hello.delivery.order.domain.OrderProductRequest;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.Stock;
import hello.delivery.store.domain.Store;
import hello.delivery.store.service.StoreServiceImpl;
import hello.delivery.user.domain.User;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    private OrderService orderService;
    private FakeFinder fakeFinder;
    private FakeProductRepository fakeProductRepository;

    public static final int ORDER_QUANTITY = 2;
    private static final int PRODUCT_PRICE = 20000;
    private static final String ADDRESS = "대구시 달서구";

    public static final LocalTime OPEN_TIME = LocalTime.of(12, 0);
    public static final LocalTime CLOSE_TIME = LocalTime.of(23, 0);

    private User customer;
    private Store store;
    private Product product;
    private Product productWithStock;

    @BeforeEach
    void setUp() {
        FakeOrderRepository fakeOrderRepository = new FakeOrderRepository();
        fakeFinder = new FakeFinder();
        fakeProductRepository = new FakeProductRepository();
        TestClockHolder testClockHolder = new TestClockHolder();
        FakeDeliveryRepository fakeDeliveryRepository = new FakeDeliveryRepository();
        FakeRiderRepository fakeRiderRepository = new FakeRiderRepository();
        StoreServiceImpl storeService = new StoreServiceImpl(new FakeStoreRepository(), fakeFinder, testClockHolder);
        DeliveryServiceImpl deliveryService = new DeliveryServiceImpl(
                fakeDeliveryRepository,
                fakeOrderRepository,
                fakeRiderRepository,
                fakeFinder,
                testClockHolder
        );

        orderService = new OrderServiceImpl(
                fakeOrderRepository,
                fakeProductRepository,
                storeService,
                deliveryService,
                fakeFinder,
                testClockHolder
        );
        setUpTestData();
    }

    private void setUpTestData() {
        User owner = buildOwner();
        customer = buildUser();
        store = buildStore(owner);
        product = buildProduct(store);
        productWithStock = buildProductWithStock(store);
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void order() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);

        // when
        Order order = orderService.order(customer.getId(), orderCreate);

        // then
        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(order.getOrderProducts().get(0).getProduct().getName()).isEqualTo("치킨");
        assertThat(order.getOrderProducts().get(0).getQuantity()).isEqualTo(ORDER_QUANTITY);
        assertThat(order.getTotalPrice()).isEqualTo(PRODUCT_PRICE * ORDER_QUANTITY);
    }

    @Test
    @DisplayName("재고가 있는 상품을 주문하면 재고가 차감된다.")
    void orderWithStock() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, productWithStock, ORDER_QUANTITY);

        // when
        Order order = orderService.order(customer.getId(), orderCreate);

        // then
        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(order.getOrderProducts().get(0).getProduct().getName()).isEqualTo("콜라");
        assertThat(order.getOrderProducts().get(0).getQuantity()).isEqualTo(ORDER_QUANTITY);
        assertThat(order.getTotalPrice()).isEqualTo(PRODUCT_PRICE * ORDER_QUANTITY);

        Product result = fakeProductRepository.findById(productWithStock.getId()).get();
        assertThat(result.getStock().getQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("주문을 수락할 수 있다.")
    void accept() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);

        //when
        Order acceptedOrder = orderService.accept(store.getOwner().getId(), order.getId());

        //then
        assertThat(acceptedOrder.getOrderStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    @DisplayName("가게 소유자가 아니면 주문을 수락할 수 없다.")
    void validateAcceptForOwner() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);

        // expect
        assertThatThrownBy(() -> orderService.accept(customer.getId(), order.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("가게 소유자만 접근할 수 있습니다.");
    }

    @Test
    @DisplayName("주문을 취소할 수 있다.")
    void cancel() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);

        //when
        Order cancelledOrder = orderService.cancel(customer.getId(), order.getId());

        //then
        assertThat(cancelledOrder.getOrderStatus()).isEqualTo(CANCELLED);
    }

    @Test
    @DisplayName("주문을 취소하면 재고가 복구된다.")
    void cancelWithIncreaseStock() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, productWithStock, 10);
        Order order = orderService.order(customer.getId(), orderCreate);

        //when
        Order cancelledOrder = orderService.cancel(customer.getId(), order.getId());

        //then
        assertThat(cancelledOrder.getOrderStatus()).isEqualTo(CANCELLED);
        Product result = fakeProductRepository.findById(productWithStock.getId()).get();
        assertThat(result.getStock().getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("사장님이 주문을 거절하면 상태가 REJECTED가 되고 재고가 복구된다.")
    void reject() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, productWithStock, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);

        // when
        Order rejectedOrder = orderService.reject(store.getOwner().getId(), order.getId());

        // then
        assertThat(rejectedOrder.getOrderStatus()).isEqualTo(REJECTED);
        Product result = fakeProductRepository.findById(productWithStock.getId()).orElseThrow();
        assertThat(result.getStock().getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("상태가 완료된 주문을 취소하면 예외를 던진다.")
    void validateCancel() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);
        Order acceptedOrder = orderService.accept(store.getOwner().getId(), order.getId());
        Order completedOrder = orderService.complete(acceptedOrder.getId());

        // expect
        assertThatThrownBy(() -> orderService.cancel(customer.getId(), completedOrder.getId()))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문을 취소할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("수락된 주문은 고객이 취소할 수 없다.")
    void validateCancelAfterAccepted() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);
        Order acceptedOrder = orderService.accept(store.getOwner().getId(), order.getId());

        // expect
        assertThatThrownBy(() -> orderService.cancel(customer.getId(), acceptedOrder.getId()))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문을 취소할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("주문을 완료할 수 있다.")
    void complete() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);
        Order acceptedOrder = orderService.accept(store.getOwner().getId(), order.getId());

        //when
        Order completedOrder = orderService.complete(acceptedOrder.getId());

        //then
        assertThat(completedOrder.getOrderStatus()).isEqualTo(COMPLETED);
    }

    @Test
    @DisplayName("상태가 수락되지 않은 주문을 완료하면 예외를 던진다.")
    void validateComplete() throws Exception {
        //given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        Order order = orderService.order(customer.getId(), orderCreate);

        // expect
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("수락된 주문만 완료 처리할 수 있습니다.");
    }

    @Test
    @DisplayName("재고가 부족한 상품을 주문하면 예외를 던진다.")
    void validateOrderOf() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, productWithStock, 20);

        // expect
        assertThatThrownBy(() -> orderService.order(customer.getId(), orderCreate))
                .isInstanceOf(StockException.class)
                .hasMessageContaining("재고가 부족합니다. 현재 재고: " + productWithStock.getStock().getQuantity());
    }

    @Test
    @DisplayName("주문한 가게에 속하지 않은 상품을 주문하면 예외를 던진다.")
    void validateProductBelongsToStore() {
        // given
        Store anotherStore = Store.builder()
                .id(2L)
                .name("교촌")
                .owner(store.getOwner())
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build();
        fakeFinder.addStore(anotherStore);

        Product anotherStoreProduct = fakeProductRepository.save(Product.builder()
                .id(3L)
                .name("허니콤보")
                .price(PRODUCT_PRICE)
                .store(anotherStore)
                .stock(Stock.of(10))
                .build());
        fakeFinder.addProduct(anotherStoreProduct);

        OrderCreate orderCreate = createOrderCreate(store, anotherStoreProduct, ORDER_QUANTITY);

        // expect
        assertThatThrownBy(() -> orderService.order(customer.getId(), orderCreate))
                .isInstanceOf(ProductException.class)
                .hasMessageContaining("주문한 가게의 상품만 주문할 수 있습니다.");
    }

    @Test
    @DisplayName("사용자 아이디로 주문 내역을 조회할 수 있다.")
    void findOrdersByUserId() {
        // given
        OrderCreate orderCreate = createOrderCreate(store, product, ORDER_QUANTITY);
        orderService.order(customer.getId(), orderCreate);

        // when
        List<Order> result = orderService.findOrdersByUserId(customer.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getName()).isEqualTo("김우섭");
        assertThat(result.get(0).getStore().getName()).isEqualTo("BBQ");
        assertThat(result.get(0).getTotalPrice()).isEqualTo(PRODUCT_PRICE * ORDER_QUANTITY);
    }

    private OrderCreate createOrderCreate(Store store, Product product, int quantity) {
        OrderProductRequest orderProduct = OrderProductRequest.builder()
                .productId(product.getId())
                .quantity(quantity)
                .build();

        return OrderCreate.builder()
                .storeId(store.getId())
                .orderProducts(List.of(orderProduct))
                .address(ADDRESS)
                .build();
    }

    private User buildOwner() {
        User owner = User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address("대구")
                .role(OWNER)
                .build();
        fakeFinder.addUser(owner);
        return owner;
    }

    private User buildUser() {
        User user = User.builder()
                .id(2L)
                .name("김우섭")
                .username("wss3454")
                .password("hihihi3454")
                .address("대구")
                .role(CUSTOMER)
                .build();
        fakeFinder.addUser(user);
        return user;
    }

    private Store buildStore(User owner) {
        Store store = Store.builder()
                .id(1L)
                .name("BBQ")
                .owner(owner)
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build();
        fakeFinder.addStore(store);
        return store;
    }

    private Product buildProduct(Store store) {
        Product savedProduct = fakeProductRepository.save(Product.builder()
                .id(1L)
                .name("치킨")
                .price(PRODUCT_PRICE)
                .store(store)
                .build());
        fakeFinder.addProduct(savedProduct);
        return savedProduct;
    }

    private Product buildProductWithStock(Store store) {
        Product savedProduct = fakeProductRepository.save(Product.builder()
                .id(2L)
                .name("콜라")
                .price(PRODUCT_PRICE)
                .store(store)
                .stock(Stock.of(10))
                .build());
        fakeFinder.addProduct(savedProduct);
        return savedProduct;
    }

}
