package hello.delivery.order.service.port.in;

import hello.delivery.order.domain.Order;
import java.util.List;

public interface OrderService {

    Order order(Long userId, OrderCreateCommand request);

    Order accept(Long ownerId, Long orderId);

    Order reject(Long ownerId, Long orderId);

    Order cancel(Long customerId, Long orderId);

    Order complete(Long orderId);

    List<Order> findOrdersByUserId(Long userId);
}
