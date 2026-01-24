package hello.delivery.store.infrastructure;

import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.product.infrastructure.ProductEntity;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.user.infrastructure.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    private String name;

    private int dailySales;

    private int totalSales;

    @Enumerated(EnumType.STRING)
    private StoreType storeType;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProductEntity> products = new ArrayList<>();

    private LocalDate openDate;

    private LocalDate lastSalesDate;

    private LocalTime openTime;

    private LocalTime closeTime;

    @Builder
    private StoreEntity(Long id, UserEntity owner, String name, int dailySales, int totalSales, StoreType storeType,
                        LocalDate openDate, LocalDate lastSalesDate, LocalTime openTime, LocalTime closeTime) {
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

    public static StoreEntity of(Store store) {
        return StoreEntity.builder()
                .id(store.getId())
                .owner(UserEntity.of(store.getOwner()))
                .name(store.getName())
                .dailySales(store.getDailySales())
                .totalSales(store.getTotalSales())
                .storeType(store.getStoreType())
                .openDate(store.getOpenDate())
                .lastSalesDate(store.getLastSalesDate())
                .openTime(store.getOpenTime())
                .closeTime(store.getCloseTime())
                .build();
    }

    public Store toDomain() {
        return Store.builder()
                .id(id)
                .owner(owner.toDomain())
                .name(name)
                .dailySales(dailySales)
                .totalSales(totalSales)
                .storeType(storeType)
                .openDate(openDate)
                .lastSalesDate(lastSalesDate)
                .openTime(openTime)
                .closeTime(closeTime)
                .build();
    }
}
