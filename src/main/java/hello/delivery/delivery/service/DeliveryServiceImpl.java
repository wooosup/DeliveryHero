package hello.delivery.delivery.service;

import static hello.delivery.rider.domain.RiderStatus.*;

import hello.delivery.common.exception.ForbiddenException;
import hello.delivery.common.exception.DeliveryException;
import hello.delivery.common.service.port.ClockHolder;
import hello.delivery.common.service.port.FinderPort;
import hello.delivery.delivery.controller.port.DeliveryService;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.DeliveryRepository;
import hello.delivery.order.domain.Order;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderStatus;
import hello.delivery.rider.domain.RiderStatusUpdate;
import hello.delivery.rider.service.port.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;
    private final FinderPort finderPort;
    private final ClockHolder clockHolder;

    @Transactional
    public void createDeliveryForOrder(Order order) {
        Delivery delivery = Delivery.create(order);

        deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery assign(Long id, Long riderId) {
        Delivery delivery = finderPort.findByDelivery(id);
        Rider rider = finderPort.findByRider(riderId);

        rider.validateAvailable();

        delivery = delivery.assign(rider.getId());
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery start(Long id, Long riderId) {
        Delivery delivery = finderPort.findByDelivery(id);
        Rider rider = finderPort.findByRider(riderId);
        validateAssignedRider(delivery, rider.getId());

        rider.validateCanStartDelivery();

        changeRiderStatus(rider, DELIVERING);

        delivery = delivery.start(rider.getId(), clockHolder);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery complete(Long id, Long riderId) {
        Delivery delivery = finderPort.findByDelivery(id);
        Rider rider = finderPort.findByRider(riderId);
        validateAssignedRider(delivery, rider.getId());

        rider.validateCanCompleteDelivery();

        changeRiderStatus(rider, AVAILABLE);

        delivery = delivery.complete(rider.getId(), clockHolder);
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery findById(Long riderId, Long id) {
        Delivery delivery = finderPort.findByDelivery(id);
        validateAssignedRider(delivery, riderId);
        return delivery;
    }

    @Override
    public Delivery findByOrderId(Long riderId, Long id) {
        Delivery delivery = deliveryRepository.findByOrderId(id)
                .orElseThrow(() -> new DeliveryException("해당 주문의 배달 정보를 찾을 수 없습니다."));
        validateAssignedRider(delivery, riderId);
        return delivery;
    }

    private void changeRiderStatus(Rider rider, RiderStatus newStatus) {
        riderRepository.save(rider.changeStatus(RiderStatusUpdate.builder()
                .status(newStatus)
                .build()));
    }

    private void validateAssignedRider(Delivery delivery, Long riderId) {
        if (delivery.getRiderId() == null || !delivery.getRiderId().equals(riderId)) {
            throw new ForbiddenException("해당 배달의 담당 라이더만 접근할 수 있습니다.");
        }
    }

}
