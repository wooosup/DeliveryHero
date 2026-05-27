package hello.delivery.order.service.port.in;

import hello.delivery.order.query.OrderQueryResult;

import java.util.List;

public interface OrderQueryService {

    List<OrderQueryResult> findOrdersByUserId(Long userId);

}
