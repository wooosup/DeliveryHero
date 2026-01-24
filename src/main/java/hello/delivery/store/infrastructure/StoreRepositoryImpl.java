package hello.delivery.store.infrastructure;

import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.StoreRepository;
import hello.delivery.user.domain.User;
import hello.delivery.user.infrastructure.UserEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository storeJpaRepository;

    @Override
    public Store save(Store store) {
        return storeJpaRepository.save(StoreEntity.of(store)).toDomain();
    }

    @Override
    public Optional<Store> findById(Long id) {
        return storeJpaRepository.findById(id)
                .map(StoreEntity::toDomain);
    }

    @Override
    public List<Store> findByStoreType(StoreType storeType) {
        return storeJpaRepository.findByStoreType(storeType).stream()
                .map(StoreEntity::toDomain)
                .toList();
    }

    @Override
    public List<Store> findAll() {
        return storeJpaRepository.findAll().stream()
                .map(StoreEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Store> findByName(String name) {
        return storeJpaRepository.findByName(name)
                .map(StoreEntity::toDomain);
    }

    @Override
    public void updateSales(Long storeId, int dailySales, int totalSales, LocalDate lastSalesDate, LocalTime openTime, LocalTime closeTime) {
        storeJpaRepository.updateSales(storeId, dailySales, totalSales, lastSalesDate, openTime, closeTime);
    }

    @Override
    public boolean existsByName(String name) {
        return storeJpaRepository.existsByName(name);
    }

    @Override
    public List<Store> findByOwner(User owner) {
        return storeJpaRepository.findByStoresForOwner(UserEntity.of(owner)).stream()
                .map(StoreEntity::toDomain)
                .toList();
    }

}
