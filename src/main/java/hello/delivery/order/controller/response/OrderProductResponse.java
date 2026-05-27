package hello.delivery.order.controller.response;

import hello.delivery.order.domain.OrderProduct;
import hello.delivery.order.query.OrderProductQueryResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderProductResponse {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final int price;
    private final int totalPrice;

    @Builder
    private OrderProductResponse(Long productId, String productName, int quantity, int price, int totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public static OrderProductResponse of(OrderProduct orderProduct) {
        return create(
                orderProduct.getProduct().getId(),
                orderProduct.getProduct().getName(),
                orderProduct.getQuantity(),
                orderProduct.getPrice().getAmount(),
                orderProduct.calculatePrice().getAmount()
        );
    }

    public static OrderProductResponse from(OrderProductQueryResult orderProduct) {
        return create(
                orderProduct.productId(),
                orderProduct.productName(),
                orderProduct.quantity(),
                orderProduct.price(),
                orderProduct.totalPrice()
        );
    }

    private static OrderProductResponse create(Long productId, String productName, int quantity, int price,
                                               int totalPrice) {
        return OrderProductResponse.builder()
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .totalPrice(totalPrice)
                .build();
    }

    public static List<OrderProductResponse> fromQueryResults(List<OrderProductQueryResult> orderProducts) {
        return orderProducts.stream()
                .map(OrderProductResponse::from)
                .toList();
    }

}
