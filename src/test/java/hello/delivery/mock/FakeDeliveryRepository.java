package hello.delivery.mock;

import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.DeliveryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeDeliveryRepository implements DeliveryRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<Delivery> data = new ArrayList<>();

    @Override
    public Delivery save(Delivery delivery) {
        if (delivery.getId() == null) {
            Delivery newDelivery = Delivery.builder()
                    .id(autoIncrement.getAndIncrement())
                    .orderId(delivery.getOrderId())
                    .riderId(delivery.getRiderId())
                    .address(delivery.getAddress())
                    .status(delivery.getStatus())
                    .startedAt(delivery.getStartedAt())
                    .completedAt(delivery.getCompletedAt())
                    .build();
            data.add(newDelivery);
            return newDelivery;
        } else {
            data.removeIf(d -> d.getId().equals(delivery.getId()));
            data.add(delivery);
            return delivery;
        }
    }

    @Override
    public Optional<Delivery> findById(Long id) {
        return data.stream()
                .filter(delivery -> delivery.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Delivery> findByOrderId(Long orderId) {
        return data.stream()
                .filter(delivery -> delivery.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public Optional<Delivery> findByIdWithLock(Long id) {
        return findById(id);
    }
}
