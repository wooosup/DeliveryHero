package hello.delivery.order.infrastructure;

import hello.delivery.order.domain.OrderStatus;
import java.time.LocalDateTime;

public record OrderQueryRow(
        Long orderId,
        Long userId,
        String userName,
        Long storeId,
        int totalPrice,
        String address,
        String storeName,
        OrderStatus orderStatus,
        LocalDateTime orderedAt,
        Long productId,
        String productName,
        int quantity,
        int price
) {
}
