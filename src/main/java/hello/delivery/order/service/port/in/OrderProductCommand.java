package hello.delivery.order.service.port.in;

import hello.delivery.common.exception.OrderException;

public record OrderProductCommand(
        Long productId,
        int quantity
) {

    public OrderProductCommand {
        if (productId == null) {
            throw new OrderException("상품 ID는 필수입니다.");
        }
        if (quantity <= 0) {
            throw new OrderException("상품 개수는 1개 이상이어야 합니다.");
        }
    }

    public static OrderProductCommand of(Long productId, int quantity) {
        return new OrderProductCommand(productId, quantity);
    }

}
