package hello.delivery.delivery.service;

import hello.delivery.common.exception.DeliveryNotFound;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.out.DeliveryFinder;
import hello.delivery.delivery.service.port.out.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryFinderImpl implements DeliveryFinder {

    private final DeliveryRepository deliveryRepository;

    @Override
    public Delivery findByDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(DeliveryNotFound::new);
    }

}
