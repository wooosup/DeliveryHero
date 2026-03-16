package hello.delivery.order.domain;

import static hello.delivery.order.domain.OrderStatus.ACCEPTED;
import static hello.delivery.order.domain.OrderStatus.CANCELLED;
import static hello.delivery.order.domain.OrderStatus.COMPLETED;
import static hello.delivery.order.domain.OrderStatus.PENDING;
import static hello.delivery.order.domain.OrderStatus.REJECTED;
import static hello.delivery.user.domain.UserRole.CUSTOMER;

import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.common.exception.OrderException;
import hello.delivery.delivery.domain.DeliveryAddress;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Order {

    private final Long id;
    private final int totalPrice;
    private final User user;
    private final Store store;
    private final DeliveryAddress address;
    private final LocalDateTime orderedAt;
    private final List<OrderProduct> orderProducts;
    private final OrderStatus orderStatus;

    @Builder
    private Order(Long id, User user, Store store, DeliveryAddress address, LocalDateTime orderedAt,
                  List<OrderProduct> orderProducts,
                  OrderStatus orderStatus) {
        validateUserAndStore(user, store);
        validate(orderProducts);
        validateStoresOpen(store, orderedAt);
        this.id = id;
        this.user = user;
        this.store = store;
        this.address = address;
        this.orderedAt = orderedAt;
        this.orderProducts = orderProducts.stream()
                .map(o -> o.getOrder() == null ? o.withOrder(this) : o)
                .toList();
        this.orderStatus = orderStatus;
        this.totalPrice = calculateTotalPrice();
    }

    public static Order order(User user, Store store, List<OrderProduct> orderProducts, String address,
                              LocalDateTime orderedAt) {
        return Order.builder()
                .user(user)
                .store(store)
                .address(DeliveryAddress.of(address))
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .orderStatus(PENDING)
                .build();
    }

    public Order accept() {
        if (this.orderStatus != PENDING) {
            throw new OrderException("주문을 수락할 수 없는 상태입니다.");
        }
        return copyWithBuilder()
                .orderStatus(ACCEPTED)
                .build();
    }

    public Order cancel() {
        if (orderStatus != PENDING) {
            throw new OrderException("주문을 취소할 수 없는 상태입니다.");
        }
        return copyWithBuilder()
                .orderStatus(CANCELLED)
                .build();
    }

    public Order reject() {
        if (this.orderStatus != PENDING) {
            throw new OrderException("주문을 거절할 수 없는 상태입니다.");
        }
        return copyWithBuilder()
                .orderStatus(REJECTED)
                .build();
    }

    public Order complete() {
        if (this.orderStatus != ACCEPTED) {
            throw new OrderException("수락된 주문만 완료 처리할 수 있습니다.");
        }
        return copyWithBuilder()
                .orderStatus(COMPLETED)
                .build();
    }

    public void validateOwner(Long ownerId) {
        if (!this.store.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("가게 소유자만 접근할 수 있습니다.");
        }
    }

    public void validateCustomer(Long customerId) {
        if (!this.user.getId().equals(customerId)) {
            throw new ForbiddenException("주문한 고객만 접근할 수 있습니다.");
        }
    }

    private void validateStoresOpen(Store store, LocalDateTime orderedAt) {
        if (!store.isOpening(orderedAt.toLocalTime())) {
            throw new OrderException("가게가 현재 영업중이 아닙니다.");
        }
    }

    private void validateUserAndStore(User user, Store store) {
        if (user == null) {
            throw new OrderException("주문하는 사용자는 필수입니다.");
        }
        if (user.getRole() != CUSTOMER) {
            throw new OrderException("주문자는 고객이어야 합니다.");
        }
        if (store == null) {
            throw new OrderException("주문하는 가게는 필수입니다.");
        }
    }

    private void validate(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new OrderException("주문에는 최소 1개 이상의 상품이 포함되어야 합니다.");
        }
    }

    private int calculateTotalPrice() {
        return orderProducts.stream()
                .mapToInt(OrderProduct::calculatePrice)
                .sum();
    }

    private OrderBuilder copyWithBuilder() {
        return Order.builder()
                .id(id)
                .user(user)
                .store(store)
                .address(address)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .orderStatus(orderStatus);
    }

}
