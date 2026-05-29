package hello.delivery.mock;

import hello.delivery.order.domain.event.OrderAcceptedEvent;
import hello.delivery.order.service.port.out.OrderEventPublisher;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FakeOrderEventPublisher implements OrderEventPublisher {

    private final List<OrderAcceptedEvent> events = new ArrayList<>();

    @Override
    public void publish(OrderAcceptedEvent event) {
        events.add(event);
    }

}
