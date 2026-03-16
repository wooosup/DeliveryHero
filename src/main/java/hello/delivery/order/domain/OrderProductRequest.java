package hello.delivery.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductRequest {

    @NotNull(message = "상품 ID는 필수 입력 값입니다.")
    @Positive(message = "상품 ID는 양수여야 합니다.")
    private final Long productId;

    @NotNull(message = "상품 개수는 필수 입력 값입니다.")
    @Positive(message = "상품 개수는 양수여야 합니다.")
    private final int quantity;

    @Builder
    private OrderProductRequest(
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
