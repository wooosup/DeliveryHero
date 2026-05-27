package hello.delivery.order.infrastructure;

import hello.delivery.order.query.OrderProductQueryResult;
import hello.delivery.order.query.OrderQueryResult;
import hello.delivery.order.service.port.out.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final OrderJpaQueryRepository orderJpaQueryRepository;

    @Override
    public Optional<OrderQueryResult> findById(Long id) {
        return toQueryResults(orderJpaQueryRepository.findRowsById(id)).stream()
                .findFirst();
    }

    @Override
    public List<OrderQueryResult> findOrdersByUserId(long userId) {
        return toQueryResults(orderJpaQueryRepository.findRowsByUserId(userId));
    }

    private List<OrderQueryResult> toQueryResults(List<OrderQueryRow> rows) {
        LinkedHashMap<Long, List<OrderQueryRow>> rowsByOrder = new LinkedHashMap<>();
        for (OrderQueryRow row : rows) {
            rowsByOrder.computeIfAbsent(row.orderId(), ignored -> new ArrayList<>()).add(row);
        }

        return rowsByOrder.values()
                .stream()
                .map(this::toQueryResult)
                .toList();
    }

    private OrderQueryResult toQueryResult(List<OrderQueryRow> rows) {
        OrderQueryRow first = rows.get(0);
        List<OrderProductQueryResult> orderProducts = rows.stream()
                .map(row -> new OrderProductQueryResult(
                        row.productId(),
                        row.productName(),
                        row.quantity(),
                        row.price()
                ))
                .toList();

        return new OrderQueryResult(
                first.orderId(),
                first.userId(),
                first.userName(),
                first.storeId(),
                first.totalPrice(),
                first.address(),
                first.storeName(),
                first.orderStatus().getDescription(),
                first.orderedAt(),
                orderProducts
        );
    }

}
