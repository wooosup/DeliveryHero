package hello.delivery.delivery.domain;

import static hello.delivery.delivery.domain.DeliveryStatus.ASSIGNED;
import static hello.delivery.delivery.domain.DeliveryStatus.DELIVERED;
import static hello.delivery.delivery.domain.DeliveryStatus.PENDING;
import static hello.delivery.delivery.domain.DeliveryStatus.PICKED_UP;
import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.DeliveryException;
import hello.delivery.mock.TestClockHolder;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderStatus;
import hello.delivery.order.domain.OrderProduct;
import hello.delivery.rider.domain.Rider;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    private static final LocalDateTime ORDERED_AT = LocalDateTime.of(2025, 11, 23, 12, 30);
    private static final LocalTime OPEN_TIME = LocalTime.of(12, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 0);

    @Test
    @DisplayName("배달을 생성 할 수 있다.")
    void create() throws Exception {
        // given
        DeliveryAddress address = createAddress();
        Order order = buildOrder(address, OrderStatus.ACCEPTED);

        // when
        Delivery delivery = Delivery.create(order);

        // then
        assertThat(delivery.getOrderId()).isEqualTo(order.getId());
        assertThat(delivery.getAddress()).isEqualTo(order.getAddress());
        assertThat(delivery.getStatus()).isEqualTo(PENDING);
    }

    @Test
    @DisplayName("주소 없이 배달을 생성하면 예외를 던진다.")
    void validateCreate() throws Exception {
        // given
        Order order = buildOrder(null, OrderStatus.ACCEPTED);

        assertThatThrownBy(() -> Delivery.create(order))
                .isInstanceOf(DeliveryException.class)
                .hasMessageContaining("배달에 필요한 주소 정보가 없습니다.");
    }

    @Test
    @DisplayName("수락되지 않은 주문으로는 배달을 생성할 수 없다.")
    void validateCreateForPendingOrder() {
        // given
        Order order = buildOrder(createAddress(), OrderStatus.PENDING);

        // expect
        assertThatThrownBy(() -> Delivery.create(order))
                .isInstanceOf(DeliveryException.class)
                .hasMessageContaining("수락된 주문만 배달을 생성할 수 있습니다.");
    }

    @Test
    @DisplayName("ASSIGNED 상태에서 배달을 시작하면 PICKED_UP 상태가 되고 시작 시간이 기록된다.")
    void start() throws Exception {
        // given
        TestClockHolder testClockHolder = new TestClockHolder();
        Order order = buildOrder(createAddress(), OrderStatus.ACCEPTED);
        Rider rider = buildRider();
        Delivery delivery = Delivery.builder()
                .address(createAddress())
                .orderId(order.getId())
                .riderId(rider.getId())
                .status(ASSIGNED)
                .startedAt(null)
                .build();

        // when
        Delivery result = delivery.start(rider.getId(), testClockHolder);

        // then
        assertThat(result.getStatus()).isEqualTo(PICKED_UP);
        assertThat(result.getStartedAt()).isEqualTo(testClockHolder.nowDateTime());
        assertThat(result).isNotSameAs(delivery);
    }

    @Test
    @DisplayName("ASSIGNED 상태가 아니면 배달을 시작할 수 없다.")
    void validateStart() {
        // given
        TestClockHolder testClockHolder = new TestClockHolder();
        Rider rider = buildRider();
        Delivery delivery = Delivery.builder()
                .address(createAddress())
                .orderId(buildOrder(createAddress(), OrderStatus.ACCEPTED).getId())
                .riderId(rider.getId())
                .status(PENDING)
                .build();

        // when & then
        assertThatThrownBy(() -> delivery.start(rider.getId(), testClockHolder))
                .isInstanceOf(DeliveryException.class)
                .hasMessage("배달을 시작할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("PICKED_UP 상태에서 배달을 완료하면 DELIVERED 상태가 되고 완료 시간이 기록된다.")
    void complete() {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 11, 23, 14, 30);
        TestClockHolder testClockHolder = new TestClockHolder();
        Rider rider = buildRider();

        Delivery delivery = Delivery.builder()
                .address(createAddress())
                .orderId(buildOrder(createAddress(), OrderStatus.ACCEPTED).getId())
                .riderId(rider.getId())
                .status(PICKED_UP)
                .startedAt(startTime)
                .build();

        // when
        Delivery completedDelivery = delivery.complete(rider.getId(), testClockHolder);

        // then
        assertThat(completedDelivery.getStatus()).isEqualTo(DELIVERED);
        assertThat(completedDelivery.getStartedAt()).isEqualTo(startTime);
        assertThat(completedDelivery.getCompletedAt()).isEqualTo(testClockHolder.nowDateTime());
    }

    @Test
    @DisplayName("PICKED_UP 상태가 아니면 배달을 완료할 수 없다.")
    void validateComplete() {
        // given
        TestClockHolder testClockHolder = new TestClockHolder();
        Rider rider = buildRider();
        Delivery delivery = Delivery.builder()
                .address(createAddress())
                .orderId(buildOrder(createAddress(), OrderStatus.ACCEPTED).getId())
                .riderId(rider.getId())
                .status(ASSIGNED)
                .build();

        // when & then
        assertThatThrownBy(() -> delivery.complete(rider.getId(), testClockHolder))
                .isInstanceOf(DeliveryException.class)
                .hasMessage("배달을 완료할 수 없는 상태입니다.");
    }

    private DeliveryAddress createAddress() {
        return DeliveryAddress.of("서울시 강남구");
    }

    private OrderProduct buildOrderProduct() {
        return OrderProduct.builder()
                .id(1L)
                .build();
    }

    private Order buildOrder(DeliveryAddress address, OrderStatus orderStatus) {
        User user = buildUser();
        Store store = buildStore();

        return Order.builder()
                .id(1L)
                .user(user)
                .store(store)
                .address(address)
                .orderedAt(ORDERED_AT)
                .orderProducts(List.of(buildOrderProduct()))
                .orderStatus(orderStatus)
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(2L)
                .name("김우섭")
                .username("wss3454")
                .password("hihihi3454")
                .address("대구")
                .role(CUSTOMER)
                .build();
    }

    private Store buildStore() {
        return Store.builder()
                .id(1L)
                .name("BBQ")
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build();
    }

    private Rider buildRider() {
        return Rider.builder()
                .id(1L)
                .name("없을무")
                .phone("010-1234-5678")
                .build();
    }

}
