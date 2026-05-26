package hello.delivery.store.service.port.in;

import hello.delivery.store.domain.StoreRegistration;
import hello.delivery.store.domain.StoreType;
import java.time.LocalDate;
import java.time.LocalTime;

public record StoreCreateCommand(
        String storeName,
        StoreType storeType,
        LocalTime openTime,
        LocalTime closeTime
) {

    public static StoreCreateCommand of(
            String storeName,
            StoreType storeType,
            LocalTime openTime,
            LocalTime closeTime
    ) {
        return new StoreCreateCommand(storeName, storeType, openTime, closeTime);
    }

    public StoreRegistration toRegistration(LocalDate openDate) {
        return new StoreRegistration(storeName, storeType, openTime, closeTime, openDate);
    }

}
