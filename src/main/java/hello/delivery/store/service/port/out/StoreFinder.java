package hello.delivery.store.service.port.out;

import hello.delivery.store.domain.Store;

public interface StoreFinder {

    Store findByStore(Long id);

    Store findByStoreName(String storeName);

}
