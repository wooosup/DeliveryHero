package hello.delivery.product.controller.request;

import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.in.ProductCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreate(

        @NotBlank(message = "가게 이름은 필수 입력 값입니다.")
        String storeName,
        @NotBlank(message = "상품 이름은 필수 입력 값입니다.")
        String name,
        @NotNull(message = "상품 가격은 필수 입력 값입니다.")
        @Positive(message = "상품 가격은 양수여야 합니다.")
        int price,
        @NotNull(message = "상품 타입은 필수 입력 값입니다.")
        ProductType type,
        @PositiveOrZero(message = "상품 재고는 0 이상이어야 합니다.")
        Integer stock

) {

    public ProductCreateCommand toCommand() {
        return ProductCreateCommand.of(
                storeName,
                name,
                price,
                type,
                stock
        );
    }

}
