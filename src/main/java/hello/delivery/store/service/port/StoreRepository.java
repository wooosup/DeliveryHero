package hello.delivery.store.service.port;

import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.user.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StoreRepository {

    Store save(Store store);

    Optional<Store> findById(Long id);

    List<Store> findByStoreType(StoreType storeType);

    List<Store> findAll();

    Optional<Store> findByName(String name);

    void updateSales(Long storeId, int dailySales, int totalSales, LocalDate lastSalesDate, LocalTime openTime, LocalTime closeTime);

    boolean existsByName(String name);

    List<Store> findByOwner(User owner);

}