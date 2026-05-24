package hello.delivery.order.controller.response;

import hello.delivery.order.domain.OrderProduct;
import lombok.Builder;
import lombok.Getter;

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
        return OrderProductResponse.builder()
                .productId(orderProduct.getProduct().getId())
                .productName(orderProduct.getProduct().getName())
                .quantity(orderProduct.getQuantity())
                .price(orderProduct.getPrice().getAmount())
                .totalPrice(orderProduct.calculatePrice().getAmount())
                .build();
    }
}
