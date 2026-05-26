package hello.delivery.product.controller.request;

import hello.delivery.product.domain.ProductSellingStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdate(
        @NotNull(message = "상품 판매 상태는 필수 입력 값입니다.")
        ProductSellingStatus status
) {
}
