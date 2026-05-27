package hello.delivery.order.query;

import java.time.LocalDateTime;
import java.util.List;

public record OrderQueryResult(
        Long id,
        Long userId,
        String userName,
        Long storeId,
        int totalPrice,
        String address,
        String storeName,
        String orderStatus,
        LocalDateTime orderedAt,
        List<OrderProductQueryResult> orderProducts
) {

    public OrderQueryResult {
        orderProducts = List.copyOf(orderProducts);
    }

}
