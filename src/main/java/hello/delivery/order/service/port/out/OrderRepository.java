package hello.delivery.order.service.port.out;

import hello.delivery.order.domain.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findOrdersByUserId(long userId);

}
