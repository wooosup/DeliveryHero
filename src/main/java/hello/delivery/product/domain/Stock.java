package hello.delivery.product.domain;

import hello.delivery.common.exception.StockException;
import lombok.Builder;
import lombok.Getter;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;
import static hello.delivery.product.domain.ProductSellingStatus.SOLD_OUT;

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
        if (amount <= 0) {
            throw new StockException("차감 수량은 1 이상이어야 합니다.");
        }
        if (this.quantity < amount) {
            throw new StockException("재고가 부족합니다. 현재 재고: " + this.quantity);
        }
        return Stock.builder()
                .quantity(this.quantity - amount)
                .build();
    }

    public ProductSellingStatus isSoldOut(ProductSellingStatus productSellingStatus) {
        if (quantity <= 0) {
            return SOLD_OUT;
        }
        return productSellingStatus;
    }

    private static void validate(int quantity) {
        if (quantity < 0) {
            throw new StockException("재고 수량은 음수일 수 없습니다.");
        }
    }

    public Stock increase(int quantity) {
        return Stock.builder()
                .quantity(this.quantity + quantity)
                .build();
    }

    public ProductSellingStatus isSelling(ProductSellingStatus productSellingStatus) {
        if (quantity > 0) {
            return SELLING;
        }
        return productSellingStatus;
    }

}
