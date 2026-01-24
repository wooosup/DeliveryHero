package hello.delivery.store.controller.response;

import hello.delivery.common.service.port.ClockHolder;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreCustomerResponse {

    private final Long id;
    private final String name;
    private final StoreType storeType;
    private final String address;
    private final boolean isOpen;
    private final LocalTime openTime;
    private final LocalTime closeTime;

    @Builder
    private StoreCustomerResponse(Long id, String name, StoreType storeType, String address, boolean isOpen,
                                  LocalTime openTime, LocalTime closeTime) {
        this.id = id;
        this.name = name;
        this.storeType = storeType;
        this.address = address;
        this.isOpen = isOpen;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public static StoreCustomerResponse of(Store store, ClockHolder clockHolder) {
        return StoreCustomerResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .storeType(store.getStoreType())
                .address(store.getOwner().getAddress())
                .isOpen(store.isOpening(clockHolder.nowTime()))
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .build();
    }

    public static List<StoreCustomerResponse> of(List<Store> stores, ClockHolder clockHolder) {
        return stores.stream()
                .map(store -> StoreCustomerResponse.of(store, clockHolder))
                .toList();
    }

}
