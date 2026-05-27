package hello.delivery.order.query;

public record OrderProductQueryResult(
        Long productId,
        String productName,
        int quantity,
        int price
) {

    public int totalPrice() {
        return price * quantity;
    }

}
