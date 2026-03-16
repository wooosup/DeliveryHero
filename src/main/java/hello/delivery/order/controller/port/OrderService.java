package hello.delivery.order.controller.port;

import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderCreate;
import java.util.List;

public interface OrderService {

    Order order(Long userId, OrderCreate request);

    Order accept(Long ownerId, Long orderId);

    Order reject(Long ownerId, Long orderId);

    Order cancel(Long customerId, Long orderId);

    Order complete(Long orderId);

    List<Order> findOrdersByUserId(Long userId);

}
