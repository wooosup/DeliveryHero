package hello.delivery.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import hello.delivery.product.domain.ProductCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreCreate {

    @NotBlank(message = "가게이름은 필수 입력 값입니다.")
    private final String storeName;

    @NotNull(message = "가게타입은 필수 입력 값입니다.")
    private final StoreType storeType;

    private final List<ProductCreate> products;

    @NotNull(message = "오픈 시간은 필수 입력 값입니다.")
    private final LocalTime openTime;

    @NotNull(message = "마감 시간은 필수 입력 값입니다.")
    private final LocalTime closeTime;

    @Builder
    private StoreCreate(
            @JsonProperty("storeName") String storeName,
            @JsonProperty("storeType") StoreType storeType,
            @JsonProperty("products") List<ProductCreate> products,
            @JsonProperty("openTime") LocalTime openTime,
            @JsonProperty("closeTime") LocalTime closeTime) {
        this.storeName = storeName;
        this.storeType = storeType;
        this.products = products;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

}
