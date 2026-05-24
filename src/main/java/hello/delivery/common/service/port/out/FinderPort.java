package hello.delivery.common.service.port.out;

import hello.delivery.delivery.domain.Delivery;
import hello.delivery.product.domain.Product;
import hello.delivery.rider.domain.Rider;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;

public interface FinderPort {

    Product findByProduct(Long id);
    Store findByStore(Long id);
    Store findByStoreName(String storeName);
    User findByUser(Long id);
    Delivery findByDelivery(Long id);
    Rider findByRider(Long id);
}
