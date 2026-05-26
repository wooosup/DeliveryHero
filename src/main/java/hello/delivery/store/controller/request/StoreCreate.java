package hello.delivery.store.controller.request;

import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.in.StoreCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record StoreCreate(
        @NotBlank(message = "가게이름은 필수 입력 값입니다.")
        String storeName,

        @NotNull(message = "가게타입은 필수 입력 값입니다.")
        StoreType storeType,

        @NotNull(message = "오픈 시간은 필수 입력 값입니다.")
        LocalTime openTime,

        @NotNull(message = "마감 시간은 필수 입력 값입니다.")
        LocalTime closeTime
) {

    public StoreCreateCommand toCommand() {
        return StoreCreateCommand.of(
                storeName,
                storeType,
                openTime,
                closeTime
        );
    }

}
