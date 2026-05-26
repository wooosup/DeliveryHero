package hello.delivery.order.controller.request;

import hello.delivery.order.service.port.in.OrderProductCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderProductRequest(
        @NotNull(message = "상품 ID는 필수 입력 값입니다.")
        @Positive(message = "상품 ID는 양수여야 합니다.")
        Long productId,

        @NotNull(message = "상품 개수는 필수 입력 값입니다.")
        @Positive(message = "상품 개수는 양수여야 합니다.")
        Integer quantity
) {

    public OrderProductCommand toCommand() {
        return OrderProductCommand.of(productId, quantity);
    }

}
