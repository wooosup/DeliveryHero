package hello.delivery.delivery.service;

import static hello.delivery.delivery.domain.DeliveryStatus.ASSIGNED;
import static hello.delivery.delivery.domain.DeliveryStatus.DELIVERED;
import static hello.delivery.delivery.domain.DeliveryStatus.PENDING;
import static hello.delivery.delivery.domain.DeliveryStatus.PICKED_UP;
import static hello.delivery.rider.domain.RiderStatus.AVAILABLE;
import static hello.delivery.rider.domain.RiderStatus.DELIVERING;
import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.DeliveryException;
import hello.delivery.delivery.controller.port.DeliveryService;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.domain.DeliveryAddress;
import hello.delivery.mock.FakeDeliveryRepository;
import hello.delivery.mock.FakeFinder;
import hello.delivery.mock.FakeRiderRepository;
import hello.delivery.mock.TestClockHolder;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderProduct;
import hello.delivery.product.domain.Product;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeliveryServiceImplTest {

    private static final LocalTime OPEN_TIME = LocalTime.of(12, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 0);

    private DeliveryService deliveryService;
    private FakeDeliveryRepository fakeDeliveryRepository;
    private FakeFinder fakeFinder;
    private TestClockHolder testClockHolder;

    @BeforeEach
    void setUp() {
        fakeDeliveryRepository = new FakeDeliveryRepository();
        FakeRiderRepository fakeRiderRepository = new FakeRiderRepository();
        fakeFinder = new FakeFinder();
        testClockHolder = new TestClockHolder();
        deliveryService = new DeliveryServiceImpl(
                fakeDeliveryRepository,
                fakeRiderRepository,
                fakeFinder,
                testClockHolder
        );
    }

    private Order setUpOrder() {
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        return buildOrder(user, store, product);
    }

    @Test
    @DisplayName("주문에 대한 배달을 생성할 수 있다.")
    void createDeliveryForOrder() {
        // given
        Order order = setUpOrder();

        // when
        deliveryService.createDeliveryForOrder(order);

        // then
        Delivery delivery = fakeDeliveryRepository.findByOrderId(order.getId()).orElseThrow();
        assertThat(delivery).isNotNull();
        assertThat(delivery.getOrderId()).isEqualTo(order.getId());
        assertThat(delivery.getStatus()).isEqualTo(PENDING);
        assertThat(delivery.getAddress().getAddress()).isEqualTo("대구시 달서구");
    }

    @Test
    @DisplayName("배달을 라이더에게 할당하면 ASSIGNED 상태가 된다.")
    void assign() throws Exception {
        // given
        Order order = setUpOrder();
        Rider rider = buildRider(AVAILABLE);

        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(order.getId())
                .riderId(rider.getId())
                .address(DeliveryAddress.of("대구시 달서구"))
                .status(PENDING)
                .build();
        fakeFinder.addDelivery(delivery);

        // when
        Delivery result = deliveryService.assign(delivery.getId(), rider.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(ASSIGNED);
    }


    @Test
    @DisplayName("배달을 시작하면 PICKED_UP 상태가 되고 시작 시간이 기록된다.")
    void start() {
        // given
        Order order = setUpOrder();
        Rider rider = buildRider(AVAILABLE);

        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(order.getId())
                .riderId(rider.getId())
                .address(DeliveryAddress.of("대구시 달서구"))
                .status(ASSIGNED)
                .build();
        fakeFinder.addDelivery(delivery);

        // when
        Delivery result = deliveryService.start(delivery.getId(), rider.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(PICKED_UP);
        assertThat(result.getStartedAt()).isEqualTo(testClockHolder.nowDateTime());
    }

    @Test
    @DisplayName("배달을 완료하면 DELIVERED 상태가 되고 완료 시간이 기록된다.")
    void complete() {
        // given
        LocalDateTime startTime = testClockHolder.nowDateTime().minusMinutes(30);
        Order order = setUpOrder();
        Rider rider = buildRider(DELIVERING);

        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(order.getId())
                .riderId(rider.getId())
                .address(DeliveryAddress.of("대구시 달서구"))
                .status(PICKED_UP)
                .startedAt(startTime)
                .build();
        fakeFinder.addDelivery(delivery);

        // when
        Delivery result = deliveryService.complete(delivery.getId(), rider.getId());

        // then
        assertThat(result.getStatus()).isEqualTo(DELIVERED);
        assertThat(result.getCompletedAt()).isEqualTo(testClockHolder.nowDateTime());
    }

    @Test
    @DisplayName("ID로 배달 정보를 조회할 수 있다.")
    void findById() {
        // given
        Order order = setUpOrder();
        Rider rider = buildRider(AVAILABLE);

        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(order.getId())
                .riderId(rider.getId())
                .address(DeliveryAddress.of("대구시 달서구"))
                .status(ASSIGNED)
                .build();
        fakeFinder.addDelivery(delivery);

        // when
        Delivery result = deliveryService.findById(rider.getId(), delivery.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(delivery.getId());
        assertThat(result.getOrderId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("주문 ID로 배달 정보를 조회할 수 있다.")
    void findByOrderId() {
        // given
        Order order = setUpOrder();
        Rider rider = buildRider(AVAILABLE);
        Delivery delivery = Delivery.builder()
                .id(1L)
                .orderId(order.getId())
                .riderId(rider.getId())
                .address(DeliveryAddress.of("대구시 달서구"))
                .status(ASSIGNED)
                .build();
        fakeDeliveryRepository.save(delivery);
        fakeFinder.addDelivery(delivery);

        // when
        Delivery result = deliveryService.findByOrderId(rider.getId(), order.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getId());
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 배달 정보를 조회하면 예외가 발생한다.")
    void validateFindOrderId() {
        // expect
        assertThatThrownBy(() -> deliveryService.findByOrderId(1L, 999L))
                .isInstanceOf(DeliveryException.class)
                .hasMessage("해당 주문의 배달 정보를 찾을 수 없습니다.");
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
        Product product = Product.builder()
                .id(1L)
                .name("치킨")
                .price(20000)
                .store(store)
                .build();
        fakeFinder.addProduct(product);
        return product;
    }

    private Order buildOrder(User user, Store store, Product product) {
        OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .quantity(2)
                .build();

        return Order.builder()
                .id(1L)
                .user(user)
                .store(store)
                .address(DeliveryAddress.of("대구시 달서구"))
                .orderedAt(testClockHolder.nowDateTime())
                .orderProducts(List.of(orderProduct))
                .build();
    }

    private Rider buildRider(RiderStatus status) {
        Rider rider = Rider.builder()
                .id(1L)
                .name("없을무")
                .phone("010-1234-5678")
                .status(status)
                .build();
        fakeFinder.addRider(rider);
        return rider;
    }

}
