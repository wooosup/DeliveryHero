package hello.delivery.store.service.port.out;

import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StoreRepository {

    Store save(Store store);

    Optional<Store> findById(Long id);

    List<Store> findByStoreType(StoreType storeType);

    List<Store> findAll();

    Optional<Store> findByName(String name);

    void updateBusinessHours(Store store);

    boolean existsByName(String name);

    List<Store> findByOwner(User owner);

    int addSales(Long storeId, int amount, LocalDate currentDate);

}
