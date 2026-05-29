package hello.delivery.product.domain;

import hello.delivery.common.domain.Money;
import hello.delivery.common.exception.ProductException;
import hello.delivery.common.exception.StoreException;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import static hello.delivery.product.domain.ProductSellingStatus.SELLING;
import static hello.delivery.product.domain.ProductSellingStatus.SOLD_OUT;

@Getter
public class Product {

    private final Long id;
    private final Store store;
    private final User owner;
    private final String name;
    private final Money price;
    private final ProductType productType;
    private final ProductSellingStatus productSellingStatus;
    private final Stock stock;

    @Builder
    private Product(Long id, Store store, User owner, String name, Money price, ProductType productType,
                    ProductSellingStatus productSellingStatus, Stock stock) {
        this.id = id;
        this.store = store;
        this.owner = owner;
        this.name = name;
        this.price = price == null ? Money.zero() : price;
        this.productType = productType;
        this.productSellingStatus = productSellingStatus;
        this.stock = stock;
    }

    public static Product create(String storeName, String name, int price, ProductType type, Integer stock, Store store, User owner) {
        validate(storeName, name, price, type, store);
        return Product.builder()
                .name(name)
                .price(Money.of(price))
                .productType(type)
                .productSellingStatus(determineSellingStatus(stock))
                .store(store)
                .owner(owner)
                .stock(Stock.of(stock))
                .build();
    }

    public void validateOwner(Long ownerId) {
        if (this.store.getOwner().isNotOwner(ownerId)) {
            throw new StoreException("가게 소유자만 접근할 수 있습니다.");
        }
    }

    public Product changeSellingStatus(ProductSellingStatus status) {
        ProductSellingStatus newStatus = this.productSellingStatus.changeStatus(status);

        return copyWithBuilder()
                .productSellingStatus(newStatus)
                .build();
    }

    public Product decreaseStock(int quantity) {
        if (productSellingStatus == SOLD_OUT) {
            throw new ProductException("품절된 상품입니다.");
        }
        if (stock == null) {
            return this;
        }

        Stock newStock = this.stock.decrease(quantity);

        ProductSellingStatus newStatus = newStock.isSoldOut(productSellingStatus);

        return copyWithBuilder()
                .stock(newStock)
                .productSellingStatus(newStatus)
                .build();
    }

    private static ProductSellingStatus determineSellingStatus(Integer quantity) {
        if (quantity != null && quantity == 0) {
            return SOLD_OUT;
        }
        return SELLING;
    }

    private static void validate(String storeName, String name, int price, ProductType type, Store store) {
        if (name == null || name.isBlank()) {
            throw new ProductException("상품 이름은 필수 입력 값입니다.");
        }
        if (price <= 0) {
            throw new ProductException("상품 가격은 양수여야 합니다.");
        }
        if (type == null) {
            throw new ProductException("상품 타입은 필수 입력 값입니다.");
        }
        if (!store.getName().equals(storeName)) {
            throw new ProductException("가게가 일치하지 않습니다.");
        }
    }

    public Product increaseStock(int quantity) {
        if (stock == null) {
            return this;
        }

        Stock newStock = stock.increase(quantity);

        ProductSellingStatus newStatus = newStock.isSelling(productSellingStatus);

        return copyWithBuilder()
                .stock(newStock)
                .productSellingStatus(newStatus)
                .build();
    }

    public ProductBuilder copyWithBuilder() {
        return Product.builder()
                .id(id)
                .store(store)
                .owner(owner)
                .name(name)
                .price(price)
                .productType(productType)
                .productSellingStatus(productSellingStatus)
                .stock(stock);
    }

}
