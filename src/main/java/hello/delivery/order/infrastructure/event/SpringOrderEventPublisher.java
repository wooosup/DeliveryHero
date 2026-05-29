package hello.delivery.order.infrastructure.event;

import hello.delivery.order.domain.event.OrderAcceptedEvent;
import hello.delivery.order.service.port.out.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringOrderEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderAcceptedEvent event) {
        eventPublisher.publishEvent(event);
    }

}
