package hello.delivery.order.domain;

import hello.delivery.common.domain.Money;
import hello.delivery.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProduct {
    private final Long id;
    private final Order order;
    private final Product product;
    private final Money price;
    private final int quantity;

    @Builder
    private OrderProduct(Long id, Order order, Product product, Money price, int quantity) {
        validateQuantity(quantity);
        this.id = id;
        this.order = order;
        this.product = product;
        this.price = price == null ? Money.zero() : price;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
    }

    public static OrderProduct create(Product product, int quantity) {
        return OrderProduct.builder()
                .product(product)
                .price(product.getPrice())
                .quantity(quantity)
                .build();
    }

    public Money calculatePrice() {
        return price.multiply(quantity);
    }

    public OrderProduct withOrder(Order order) {
        return OrderProduct.builder()
                .id(id)
                .order(order)
                .product(product)
                .price(price)
                .quantity(quantity)
                .build();
    }
}
