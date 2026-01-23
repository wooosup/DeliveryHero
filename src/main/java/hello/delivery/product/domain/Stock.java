package hello.delivery.product.domain;

import hello.delivery.common.exception.StockException;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Stock {

    private final int quantity;

    @Builder
    private Stock(int quantity) {
        validate(quantity);
        this.quantity = quantity;
    }

    public static Stock of(Integer quantity) {
        if (quantity == null) {
            return null;
        }
        return Stock.builder()
                .quantity(quantity)
                .build();
    }

    public Stock decrease(int amount) {
        if (this.quantity < amount) {
            throw new StockException("재고가 부족합니다. 현재 재고: " + this.quantity);
        }
        return Stock.builder()
                .quantity(this.quantity - amount)
                .build();
    }

    public boolean isSoldOut() {
        return quantity <= 0;
    }

    private static void validate(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 음수일 수 없습니다.");
        }
    }
}
