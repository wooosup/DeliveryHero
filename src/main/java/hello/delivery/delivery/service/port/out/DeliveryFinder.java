package hello.delivery.delivery.service.port.out;

import hello.delivery.delivery.domain.Delivery;

public interface DeliveryFinder {

    Delivery findByDelivery(Long id);

}
