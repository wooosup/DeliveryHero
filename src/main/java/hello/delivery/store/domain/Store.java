package hello.delivery.store.domain;

import hello.delivery.common.exception.StoreException;
import hello.delivery.user.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Store {

    private final Long id;
    private final User owner;
    private final String name;
    private final StoreType storeType;
    private final int dailySales;
    private final int totalSales;
    private final LocalDate lastSalesDate;
    private final LocalDate openDate;
    private final LocalTime openTime;
    private final LocalTime closeTime;

    @Builder
    private Store(Long id, User owner, String name, int dailySales, int totalSales, StoreType storeType,
                  LocalDate openDate,
                  LocalDate lastSalesDate, LocalTime openTime, LocalTime closeTime) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.dailySales = dailySales;
        this.totalSales = totalSales;
        this.storeType = storeType;
        this.openDate = openDate;
        this.lastSalesDate = lastSalesDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public static Store create(StoreCreate storeCreate, User owner, LocalDate currentDate) {
        validate(storeCreate, owner);
        return Store.builder()
                .owner(owner)
                .name(storeCreate.getStoreName())
                .storeType(storeCreate.getStoreType())
                .dailySales(0)
                .totalSales(0)
                .openDate(currentDate)
                .lastSalesDate(null)
                .openTime(storeCreate.getOpenTime())
                .closeTime(storeCreate.getCloseTime())
                .build();
    }

    public Store openStore(LocalTime openTime) {
        return copyWithBuilder()
                .openTime(openTime)
                .build();
    }

    public Store closeStore(LocalTime closeTime) {
        return copyWithBuilder()
                .closeTime(closeTime)
                .build();
    }

    public boolean isOpening(LocalTime now) {
        if (openTime == null || closeTime == null) {
            return false;
        }
        if (closeTime.isBefore(openTime)) {
            return now.isAfter(openTime) || now.isBefore(closeTime);
        }
        return !now.isBefore(openTime) && !now.isAfter(closeTime);
    }

    public Store addTotalSales(int amount, LocalDate currentDate) {
        int newTotalSales = totalSales + amount;
        int newDailySales = amount;
        LocalDate newLastSalesDate = currentDate;

        if (currentDate.equals(lastSalesDate)) {
            newDailySales = dailySales + amount;
            newLastSalesDate = lastSalesDate;
        }

        return copyWithBuilder()
                .dailySales(newDailySales)
                .totalSales(newTotalSales)
                .lastSalesDate(newLastSalesDate)
                .build();
    }

    public void validateIsOwner(User user) {
        if (!this.owner.getId().equals(user.getId())) {
            throw new StoreException("가게 소유자만 접근할 수 있습니다.");
        }
    }

    private static void validate(StoreCreate storeCreate, User owner) {
        if (owner == null) {
            throw new StoreException("가게 주인은 필수입니다.");
        }
        if (storeCreate.getStoreName() == null || storeCreate.getStoreName().isBlank()) {
            throw new StoreException("가게 이름은 필수 입력 값입니다.");
        }
        if (storeCreate.getStoreType() == null) {
            throw new StoreException("가게 타입은 필수 입력 값입니다.");
        }
        owner.validateOwnerRole();
    }

    private StoreBuilder copyWithBuilder() {
        return Store.builder()
                .id(id).owner(owner).name(name).storeType(storeType)
                .dailySales(dailySales).totalSales(totalSales)
                .openDate(openDate).lastSalesDate(lastSalesDate)
                .openTime(openTime).closeTime(closeTime);
    }

}
