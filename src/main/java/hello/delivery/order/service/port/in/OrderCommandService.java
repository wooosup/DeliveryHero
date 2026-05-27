package hello.delivery.order.service.port.in;

import hello.delivery.order.domain.Order;

public interface OrderCommandService {

    Order order(Long userId, OrderCreateCommand request);

    Order accept(Long ownerId, Long orderId);

    Order reject(Long ownerId, Long orderId);

    Order cancel(Long customerId, Long orderId);

    Order complete(Long orderId);

}
