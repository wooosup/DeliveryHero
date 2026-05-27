package hello.delivery.order.service.port.out;

import hello.delivery.order.query.OrderQueryResult;
import java.util.List;
import java.util.Optional;

public interface OrderQueryRepository {

    Optional<OrderQueryResult> findById(Long id);

    List<OrderQueryResult> findOrdersByUserId(long userId);

}
