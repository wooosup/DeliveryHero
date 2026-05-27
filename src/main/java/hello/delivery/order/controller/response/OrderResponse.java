package hello.delivery.order.controller.response;

import hello.delivery.order.domain.Order;
import hello.delivery.order.query.OrderQueryResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
        return create(
                order.getId(),
                order.getStore().getId(),
                order.getTotalPrice().getAmount(),
                order.getAddress().getAddress(),
                order.getStore().getName(),
                order.getOrderStatus().getDescription(),
                order.getOrderedAt(),
                order.getOrderProducts().stream()
                        .map(OrderProductResponse::of)
                        .toList()
        );
    }

    public static OrderResponse from(OrderQueryResult result) {
        return create(
                result.id(),
                result.storeId(),
                result.totalPrice(),
                result.address(),
                result.storeName(),
                result.orderStatus(),
                result.orderedAt(),
                OrderProductResponse.fromQueryResults(result.orderProducts())
        );
    }

    public static List<OrderResponse> of(List<Order> orders) {
        return orders.stream()
                .map(OrderResponse::of)
                .toList();
    }

    public static List<OrderResponse> fromQueryResults(List<OrderQueryResult> orders) {
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

    private static OrderResponse create(Long id, Long storeId, int totalPrice, String address, String storeName,
                                        String orderStatus, LocalDateTime orderedAt,
                                        List<OrderProductResponse> orderProducts) {
        return OrderResponse.builder()
                .id(id)
                .storeId(storeId)
                .totalPrice(totalPrice)
                .address(address)
                .storeName(storeName)
                .orderStatus(orderStatus)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .build();
    }

}
