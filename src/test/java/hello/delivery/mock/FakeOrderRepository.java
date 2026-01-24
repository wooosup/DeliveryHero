package hello.delivery.mock;

import hello.delivery.order.domain.Order;
import hello.delivery.order.service.port.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeOrderRepository implements OrderRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<Order> data = new ArrayList<>();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            Order newOrder = Order.builder()
                    .id(autoIncrement.getAndIncrement())
                    .user(order.getUser())
                    .store(order.getStore())
                    .address(order.getAddress())
                    .orderedAt(order.getOrderedAt())
                    .orderProducts(order.getOrderProducts())
                    .orderStatus(order.getOrderStatus())
                    .build();
            data.add(newOrder);
            return newOrder;
        } else {
            data.removeIf(o -> o.getId().equals(order.getId()));
            data.add(order);
            return order;
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        return data.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Order> findOrdersByUserId(long userId) {
        return data.stream()
                .filter(order -> order.getUser().getId() == userId)
                .toList();
    }

}
