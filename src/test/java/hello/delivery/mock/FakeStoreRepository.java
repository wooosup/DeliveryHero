package hello.delivery.mock;

import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.StoreRepository;
import hello.delivery.user.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeStoreRepository implements StoreRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<Store> data = new ArrayList<>();

    @Override
    public Store save(Store store) {
        if (store.getId() == null) {
            Store newStore = Store.builder()
                    .id(autoIncrement.getAndIncrement())
                    .owner(store.getOwner())
                    .storeType(store.getStoreType())
                    .name(store.getName())
                    .totalSales(store.getTotalSales())
                    .openTime(store.getOpenTime())
                    .closeTime(store.getCloseTime())
                    .dailySales(store.getDailySales())
                    .openDate(store.getOpenDate())
                    .lastSalesDate(store.getLastSalesDate())
                    .build();
            data.add(newStore);
            return newStore;
        } else {
            data.removeIf(s -> s.getId().equals(store.getId()));
            data.add(store);
            return store;
        }
    }

    @Override
    public Optional<Store> findById(Long id) {
        return data.stream()
                .filter(store -> store.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Store> findByStoreType(StoreType storeType) {
        return data.stream()
                .filter(store -> store.getStoreType().equals(storeType))
                .toList();
    }

    @Override
    public List<Store> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Optional<Store> findByName(String name) {
        return data.stream()
                .filter(store -> store.getName().equals(name))
                .findAny();
    }

    @Override
    public void updateSales(Long storeId, int dailySales, int totalSales, LocalDate lastSalesDate, LocalTime openTime, LocalTime closeTime) {
        data.stream()
                .filter(store -> store.getId().equals(storeId))
                .findAny()
                .ifPresent(store -> {
                    data.remove(store);
                    Store updatedStore = Store.builder()
                            .id(store.getId())
                            .owner(store.getOwner())
                            .name(store.getName())
                            .storeType(store.getStoreType())
                            .dailySales(dailySales)
                            .totalSales(totalSales)
                            .openDate(store.getOpenDate())
                            .lastSalesDate(lastSalesDate)
                            .openTime(openTime)
                            .closeTime(closeTime)
                            .build();
                    data.add(updatedStore);
                });
    }

    @Override
    public boolean existsByName(String name) {
        return data.stream()
                .anyMatch(store -> store.getName().equals(name));
    }

    @Override
    public List<Store> findByOwner(User owner) {
        return data.stream()
                .filter(store -> store.getOwner().equals(owner))
                .toList();
    }

}
