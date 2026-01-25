package hello.delivery.order.controller.response;

import hello.delivery.order.domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private final Long id;
    private final Long storeId;
    private final int totalPrice;
    private final String address;
    private final String storeName;
    private final String orderStatus;
    private final LocalDateTime orderedAt;
    private final List<OrderProductResponse> orderProducts;

    @Builder
    private OrderResponse(Long id, Long storeId, int totalPrice, String address, String storeName, String orderStatus,
                          LocalDateTime orderedAt, List<OrderProductResponse> orderProducts) {
        this.id = id;
        this.storeId = storeId;
        this.totalPrice = totalPrice;
        this.address = address;
        this.storeName = storeName;
        this.orderStatus = orderStatus;
        this.orderedAt = orderedAt;
        this.orderProducts = orderProducts;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .storeId(order.getStore().getId())
                .totalPrice(order.getTotalPrice())
                .address(order.getAddress().getAddress())
                .storeName(order.getStore().getName())
                .orderStatus(order.getOrderStatus().getDescription())
                .orderedAt(order.getOrderedAt())
                .orderProducts(order.getOrderProducts().stream()
                        .map(OrderProductResponse::of)
                        .toList())
                .build();
    }

    public static List<OrderResponse> of(List<Order> orders) {
        return orders.stream()
                .map(OrderResponse::of)
                .toList();
    }

}
