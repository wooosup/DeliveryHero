package hello.delivery.delivery.service.port;

import hello.delivery.delivery.domain.Delivery;

import java.util.Optional;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(Long id);

    Optional<Delivery> findByOrderId(Long orderId);

    Optional<Delivery> findByIdWithLock(Long id);

}
