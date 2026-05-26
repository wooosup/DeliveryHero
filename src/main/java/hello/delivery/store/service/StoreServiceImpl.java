package hello.delivery.store.service;

import hello.delivery.common.exception.StoreException;
import hello.delivery.common.exception.StoreNotFound;
import hello.delivery.common.service.port.out.ClockHolder;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.in.StoreService;
import hello.delivery.store.service.port.out.StoreFinder;
import hello.delivery.store.service.port.out.StoreRepository;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.out.UserFinder;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreFinder storeFinder;
    private final UserFinder userFinder;
    private final ClockHolder clockHolder;

    @Transactional
    public Store create(Long userId, StoreCreate request) {
        User owner = userFinder.findByUser(userId);
        validateDuplicate(request);

        Store store = Store.create(request, owner, clockHolder.now());

        return storeRepository.save(store);
    }

    @Transactional
    public Store changeOpenTime(Long userId, Long storeId, LocalTime newOpenTime) {
        Store store = storeFinder.findByStore(storeId);
        User user = userFinder.findByUser(userId);

        store.validateIsOwner(user);

        Store updatedStore = store.openStore(newOpenTime);
        repositoryUpdate(store, updatedStore);
        return updatedStore;
    }

    @Transactional
    public Store changeCloseTime(Long userId, Long storeId, LocalTime newCloseTime) {
        Store store = storeFinder.findByStore(storeId);
        User user = userFinder.findByUser(userId);

        store.validateIsOwner(user);

        Store updatedStore = store.closeStore(newCloseTime);
        repositoryUpdate(store, updatedStore);
        return updatedStore;
    }

    @Transactional
    public void addTotalSales(Long storeId, int amount) {
        int updated = storeRepository.addSales(storeId, amount, clockHolder.now());
        if (updated == 0) {
            throw new StoreNotFound();
        }
    }

    public List<Store> findByStoreType(StoreType storeType) {
        return storeRepository.findByStoreType(storeType);
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store findByName(String name) {
        return storeRepository.findByName(name)
                .orElseThrow(StoreNotFound::new);
    }

    public List<Store> findByOwnerId(Long userId) {
        User owner = userFinder.findByUser(userId);
        owner.validateOwnerRole();

        return storeRepository.findByOwner(owner);
    }

    private void validateDuplicate(StoreCreate request) {
        if (storeRepository.existsByName(request.getStoreName())) {
            throw new StoreException("이미 존재하는 가게 이름입니다.");
        }
    }

    private void repositoryUpdate(Store store, Store updatedStore) {
        storeRepository.updateSales(
                store.getId(),
                updatedStore.getDailySales(),
                updatedStore.getTotalSales(),
                updatedStore.getLastSalesDate(),
                updatedStore.getOpenTime(),
                updatedStore.getCloseTime()
        );
    }

}
