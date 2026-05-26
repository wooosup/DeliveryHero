package hello.delivery.store.service;

import hello.delivery.common.exception.StoreNotFound;
import hello.delivery.store.domain.Store;
import hello.delivery.store.service.port.out.StoreFinder;
import hello.delivery.store.service.port.out.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreFinderImpl implements StoreFinder {

    private final StoreRepository storeRepository;

    @Override
    public Store findByStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(StoreNotFound::new);
    }

    @Override
    public Store findByStoreName(String storeName) {
        return storeRepository.findByName(storeName)
                .orElseThrow(StoreNotFound::new);
    }

}
