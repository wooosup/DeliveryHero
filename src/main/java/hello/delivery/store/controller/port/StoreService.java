package hello.delivery.store.controller.port;

import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.store.domain.StoreType;
import java.time.LocalTime;
import java.util.List;

public interface StoreService {

    Store create(Long userId, StoreCreate request);

    Store changeOpenTime(Long id, LocalTime time);

    Store changeCloseTime(Long id, LocalTime time);

    void addTotalSales(Long storeId, int amount);

    List<Store> findByStoreType(StoreType storeType);

    List<Store> findAll();

    Store findByName(String name);

    List<Store> findByOwnerId(Long userId);

}
