package hello.delivery.common.service;

import hello.delivery.common.exception.DeliveryNotFound;
import hello.delivery.common.exception.OrderNotFound;
import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.common.exception.RiderNotFound;
import hello.delivery.common.exception.StoreNotFound;
import hello.delivery.common.exception.UserNotFound;
import hello.delivery.common.service.port.FinderPort;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.DeliveryRepository;
import hello.delivery.order.domain.Order;
import hello.delivery.order.service.port.OrderRepository;
import hello.delivery.product.domain.Product;
import hello.delivery.product.service.port.ProductRepository;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.service.port.RiderRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.store.service.port.StoreRepository;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Finder implements FinderPort {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;

    @Override
    public Product findByProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFound::new);
    }

    @Override
    public Store findByStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(StoreNotFound::new);
    }

    @Override
    public User findByUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFound::new);
    }

    @Override
    public Delivery findByDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(DeliveryNotFound::new);
    }

    @Override
    public Rider findByRider(Long id) {
        return riderRepository.findById(id)
                .orElseThrow(RiderNotFound::new);
    }

    @Override
    public Order findByOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(OrderNotFound::new);
    }

    @Override
    public Store findByStoreName(String storeName) {
        return storeRepository.findByName(storeName)
                .orElseThrow(StoreNotFound::new);
    }

}
