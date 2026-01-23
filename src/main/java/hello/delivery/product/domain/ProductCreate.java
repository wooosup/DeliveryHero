package hello.delivery.product.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductCreate {

    @NotBlank(message = "가게 이름은 필수 입력 값입니다.")
    private final String storeName;

    @NotBlank(message = "상품 이름은 필수 입력 값입니다.")
    private final String name;

    @NotNull(message = "상품 가격은 필수 입력 값입니다.")
    @Positive(message = "상품 가격은 양수여야 합니다.")
    private final int price;

    @NotNull(message = "상품 타입은 필수 입력 값입니다.")
    private final ProductType type;

    private final Integer stock;

    @Builder
    private ProductCreate(
            @JsonProperty("storeName") String storeName,
            @JsonProperty("name") String name,
            @JsonProperty("price") int price,
            @JsonProperty("type") ProductType type,
            @JsonProperty("stock") Integer stock) {
        this.storeName = storeName;
        this.name = name;
        this.price = price;
        this.type = type;
        this.stock = stock;
    }

}
