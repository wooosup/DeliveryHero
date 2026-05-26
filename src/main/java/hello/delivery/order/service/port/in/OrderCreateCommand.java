package hello.delivery.order.service.port.in;

import hello.delivery.common.exception.OrderException;
import java.util.List;

public record OrderCreateCommand(
        Long storeId,
        List<OrderProductCommand> orderProducts,
        String address
) {

    public OrderCreateCommand {
        if (storeId == null) {
            throw new OrderException("가게 ID는 필수입니다.");
        }
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new OrderException("주문에는 최소 1개 이상의 상품이 포함되어야 합니다.");
        }
        if (orderProducts.stream().anyMatch(orderProduct -> orderProduct == null)) {
            throw new OrderException("주문 상품은 필수입니다.");
        }
        if (address == null || address.isBlank()) {
            throw new OrderException("배달 주소는 필수입니다.");
        }
        orderProducts = List.copyOf(orderProducts);
    }

    public static OrderCreateCommand of(Long storeId, List<OrderProductCommand> orderProducts, String address) {
        return new OrderCreateCommand(storeId, orderProducts, address);
    }

}
