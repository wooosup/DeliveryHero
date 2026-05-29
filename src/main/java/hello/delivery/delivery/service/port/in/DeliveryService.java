package hello.delivery.delivery.service.port.in;

import hello.delivery.delivery.domain.Delivery;

public interface DeliveryService {

    void createDeliveryForOrder(Long orderId);

    Delivery assign(Long id, Long riderId);

    Delivery start(Long id, Long riderId);

    Delivery complete(Long id, Long riderId);

    Delivery findById(Long riderId, Long id);

    Delivery findByOrderId(Long riderId, Long id);

}
