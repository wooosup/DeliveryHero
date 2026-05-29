package hello.delivery.order.service.port.out;

import hello.delivery.order.domain.event.OrderAcceptedEvent;

public interface OrderEventPublisher {

    void publish(OrderAcceptedEvent event);
}
