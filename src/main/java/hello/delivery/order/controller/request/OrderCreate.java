package hello.delivery.order.controller.request;

import hello.delivery.order.service.port.in.OrderCreateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record OrderCreate(
        @NotNull(message = "가게 ID는 필수 입력 값입니다.")
        @Positive(message = "가게 ID는 양수여야 합니다.")
        Long storeId,

        @NotEmpty(message = "주문 상품은 필수 입력 값입니다.")
        List<@Valid OrderProductRequest> orderProducts,

        @NotBlank(message = "배달 주소는 필수 입력 값입니다.")
        String address
) {

    public OrderCreateCommand toCommand() {
        return OrderCreateCommand.of(
                storeId,
                orderProducts.stream()
                        .map(OrderProductRequest::toCommand)
                        .toList(),
                address
        );
    }

}
