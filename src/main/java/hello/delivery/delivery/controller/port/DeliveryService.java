package hello.delivery.delivery.controller.port;

import hello.delivery.delivery.domain.Delivery;
import hello.delivery.order.domain.Order;

public interface DeliveryService {

    void createDeliveryForOrder(Order order);

    Delivery assign(Long id, Long riderId);

    Delivery start(Long id, Long riderId);

    Delivery complete(Long id, Long riderId);

    Delivery findById(Long riderId, Long id);

    Delivery findByOrderId(Long riderId, Long id);

}
