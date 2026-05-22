package hello.delivery.delivery.infrastructure;

import hello.delivery.common.exception.OrderNotFound;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.DeliveryRepository;
import hello.delivery.order.infrastructure.OrderEntity;
import hello.delivery.order.infrastructure.OrderJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository deliveryJpaRepository;
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Delivery save(Delivery delivery) {
        Long orderId = delivery.getOrderId();
        OrderEntity orderEntity = orderJpaRepository.findById(orderId)
                .orElseThrow(OrderNotFound::new);

        DeliveryEntity deliveryEntity = DeliveryEntity.of(delivery, orderEntity);
        return deliveryJpaRepository.save(deliveryEntity).toDomain();
    }

    @Override
    public Optional<Delivery> findById(Long id) {
        return deliveryJpaRepository.findById(id).map(DeliveryEntity::toDomain);
    }

    @Override
    public Optional<Delivery> findByOrderId(Long orderId) {
        return deliveryJpaRepository.findByOrderId(orderId).map(DeliveryEntity::toDomain);
    }

    @Override
    public Optional<Delivery> findByIdWithLock(Long id) {
        return deliveryJpaRepository.findByIdWithLock(id).map(DeliveryEntity::toDomain);
    }

}
