package hello.delivery.order.infrastructure;

import hello.delivery.order.domain.Order;
import hello.delivery.order.service.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.of(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id).map(OrderEntity::toDomain);
    }

    @Override
    public List<Order> findOrdersByUserId(long userId) {
        return orderJpaRepository.findOrdersByUserId(userId).stream()
                .map(OrderEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findByIdWithLock(Long id) {
        return orderJpaRepository.findByIdWithLock(id).map(OrderEntity::toDomain);
    }

}
