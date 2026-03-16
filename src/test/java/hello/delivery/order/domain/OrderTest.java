package hello.delivery.order.domain;

import static hello.delivery.order.domain.OrderStatus.*;
import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.OrderException;
import hello.delivery.product.domain.Product;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    public static final LocalDateTime ORDERED_AT = LocalDateTime.of(2026, 1, 30, 12, 0);
    public static final LocalDateTime WRONG_ORDERED_AT = LocalDateTime.of(2026, 1, 30, 11, 0);
    public static final LocalTime OPEN_TIME = LocalTime.of(12, 0);
    public static final LocalTime CLOSE_TIME = LocalTime.of(23, 0);

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void order() throws Exception {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);

        // when
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        // then
        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getStore()).isEqualTo(store);
        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualTo(40000);
        assertThat(order.getOrderStatus()).isEqualTo(PENDING);
    }

    @Test
    @DisplayName("영업시간이 아니면 예외를 던진다.")
    void validateOrderTime() throws Exception {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);

        // expect
        assertThatThrownBy(() -> Order.order(user, store, List.of(orderProduct), "주소", WRONG_ORDERED_AT))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("가게가 현재 영업중이 아닙니다.");
    }

    @Test
    @DisplayName("PENDING 상태의 주문을 수락할 수 있다.")
    void accept() throws Exception {
        //given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        //when
        Order acceptedOrder = order.accept();

        //then
        assertThat(acceptedOrder.getOrderStatus()).isEqualTo(ACCEPTED);
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 주문은 수락할 수 없다.")
    void notAccept() throws Exception {
        //given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        Order acceptedOrder = order.accept();

        //expect
        assertThatThrownBy(acceptedOrder::accept)
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문을 수락할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("주문을 취소할 수 있다.")
    void cancel() throws Exception {
        //given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        //when
        Order cancelledOrder = order.cancel();

        //then
        assertThat(cancelledOrder.getOrderStatus()).isEqualTo(CANCELLED);
    }

    @Test
    @DisplayName("주문을 거절할 수 있다.")
    void reject() {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        // when
        Order rejectedOrder = order.reject();

        // then
        assertThat(rejectedOrder.getOrderStatus()).isEqualTo(REJECTED);
    }

    @Test
    @DisplayName("ACCEPTED 상태의 주문은 고객이 취소할 수 없다.")
    void validateCancelAfterAccepted() {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);
        Order acceptedOrder = order.accept();

        // expect
        assertThatThrownBy(acceptedOrder::cancel)
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문을 취소할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 주문은 거절할 수 없다.")
    void validateReject() {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);
        Order acceptedOrder = order.accept();

        // expect
        assertThatThrownBy(acceptedOrder::reject)
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문을 거절할 수 없는 상태입니다.");
    }

    @Test
    @DisplayName("주문을 완료할 수 있다.")
    void complete() throws Exception {
        //given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);
        Order acceptedOrder = order.accept();

        //when
        Order completedOrder = acceptedOrder.complete();

        //then
        assertThat(completedOrder.getOrderStatus()).isEqualTo(COMPLETED);
    }

    @Test
    @DisplayName("ACCEPTED 상태가 아닌 주문은 완료할 수 없다.")
    void validateComplete() throws Exception {
        //given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        Order order = Order.order(user, store, List.of(orderProduct), "주소", ORDERED_AT);

        //expect
        assertThatThrownBy(order::complete)
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("수락된 주문만 완료 처리할 수 있습니다.");
    }

    @Test
    @DisplayName("사용자가 null이면 예외를 던진다.")
    void validateOrderNotUser() throws Exception {
        // given
        User owner = buildOwner();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);
        // expect
        assertThatThrownBy(() -> Order.order(null, store, List.of(orderProduct), "주소", ORDERED_AT))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문하는 사용자는 필수입니다.");
    }

    @Test
    @DisplayName("가게가 null이면 예외를 던진다.")
    void validateOrderNotStore() throws Exception {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);
        Product product = buildProduct(store);
        OrderProduct orderProduct = OrderProduct.create(product, 2);

        // expect
        assertThatThrownBy(() -> Order.order(user, null, List.of(orderProduct), "주소", ORDERED_AT))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문하는 가게는 필수입니다.");
    }

    @Test
    @DisplayName("삼품들이 null이면 예외를 던진다.")
    void validateOrderNotOrderProducts() throws Exception {
        // given
        User owner = buildOwner();
        User user = buildUser();
        Store store = buildStore(owner);

        // expect
        assertThatThrownBy(() -> Order.order(user, store, null, "주소", ORDERED_AT))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("주문에는 최소 1개 이상의 상품이 포함되어야 합니다.");
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

    private static User buildUser() {
        return User.builder()
                .id(2L)
                .name("김우섭")
                .username("wss3325")
                .password("3454")
                .address("대구")
                .role(CUSTOMER)
                .build();
    }

    private static Store buildStore(User owner) {
        return Store.builder()
                .id(1L)
                .name("BBQ")
                .owner(owner)
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build();
    }

    private static Product buildProduct(Store store) {
        return Product.builder()
                .name("치킨")
                .price(20000)
                .store(store)
                .build();
    }

}
