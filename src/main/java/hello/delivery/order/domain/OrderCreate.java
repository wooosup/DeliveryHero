package hello.delivery.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCreate {

    @NotNull(message = "가게 ID는 필수 입력 값입니다.")
    @Positive(message = "가게 ID는 양수여야 합니다.")
    private final Long storeId;

    @NotEmpty(message = "주문 상품은 필수 입력 값입니다.")
    private final List<OrderProductRequest> orderProducts;

    @NotBlank(message = "배달 주소는 필수 입력 값입니다.")
    private final String address;

    @Builder
    private OrderCreate(
            @JsonProperty("storeId") Long storeId,
            @JsonProperty("orderProducts") List<OrderProductRequest> orderProducts,
            @JsonProperty("address") String address) {
        this.storeId = storeId;
        this.orderProducts = orderProducts;
        this.address = address;
    }

}
