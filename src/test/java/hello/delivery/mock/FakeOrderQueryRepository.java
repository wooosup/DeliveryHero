package hello.delivery.mock;

import hello.delivery.order.domain.Order;
import hello.delivery.order.query.OrderProductQueryResult;
import hello.delivery.order.query.OrderQueryResult;
import hello.delivery.order.service.port.out.OrderQueryRepository;
import hello.delivery.order.service.port.out.OrderRepository;
import java.util.List;
import java.util.Optional;

public class FakeOrderQueryRepository implements OrderQueryRepository {

    private final OrderRepository orderRepository;

    public FakeOrderQueryRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<OrderQueryResult> findById(Long id) {
        return orderRepository.findById(id).map(this::toQueryResult);
    }

    @Override
    public List<OrderQueryResult> findOrdersByUserId(long userId) {
        return orderRepository.findOrdersByUserId(userId).stream()
                .map(this::toQueryResult)
                .toList();
    }

    private OrderQueryResult toQueryResult(Order order) {
        return new OrderQueryResult(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getStore().getId(),
                order.getTotalPrice().getAmount(),
                order.getAddress().getAddress(),
                order.getStore().getName(),
                order.getOrderStatus().getDescription(),
                order.getOrderedAt(),
                order.getOrderProducts().stream()
                        .map(orderProduct -> new OrderProductQueryResult(
                                orderProduct.getProduct().getId(),
                                orderProduct.getProduct().getName(),
                                orderProduct.getQuantity(),
                                orderProduct.getPrice().getAmount()
                        ))
                        .toList()
        );
    }

}
