package hello.delivery.delivery.service.event;

import hello.delivery.delivery.service.port.in.DeliveryService;
import hello.delivery.order.domain.event.OrderAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderAcceptedEventHandler {

    private final DeliveryService deliveryService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderAcceptedEvent event) {
        deliveryService.createDeliveryForOrder(event.orderId());
    }

}
