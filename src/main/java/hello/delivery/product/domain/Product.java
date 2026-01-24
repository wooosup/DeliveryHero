package hello.delivery.product.domain;

import static hello.delivery.product.domain.ProductSellingStatus.*;

import hello.delivery.common.exception.ProductException;
import hello.delivery.common.exception.StoreException;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {

    private final Long id;
    private final Store store;
    private final User owner;
    private final String name;
    private final int price;
    private final ProductType productType;
    private final ProductSellingStatus productSellingStatus;
    private final Stock stock;

    @Builder
    private Product(Long id, Store store, User owner, String name, int price, ProductType productType,
                    ProductSellingStatus productSellingStatus, Stock stock) {
        this.id = id;
        this.store = store;
        this.owner = owner;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.productSellingStatus = productSellingStatus;
        this.stock = stock;
    }

    public static Product of(ProductCreate productCreate, Store store, User owner) {
        validate(productCreate, store);
        return Product.builder()
                .name(productCreate.getName())
                .price(productCreate.getPrice())
                .productType(productCreate.getType())
                .productSellingStatus(determineSellingStatus(productCreate.getStock()))
                .store(store)
                .owner(owner)
                .stock(Stock.of(productCreate.getStock()))
                .build();
    }

    public void validateOwner(Long ownerId) {
        if (this.store.getOwner().isNotOwner(ownerId)) {
            throw new StoreException("가게 소유자만 접근할 수 있습니다.");
        }
    }

    public Product changeSellingStatus(ProductSellingStatus status) {
        ProductSellingStatus newStatus = this.productSellingStatus.changeStatus(status);
        return Product.builder()
                .id(id)
                .store(store)
                .name(name)
                .price(price)
                .productType(productType)
                .productSellingStatus(newStatus)
                .owner(owner)
                .stock(stock)
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

        return Product.builder()
                .id(id)
                .store(store)
                .name(name)
                .price(price)
                .productType(productType)
                .productSellingStatus(newStatus)
                .owner(owner)
                .stock(newStock)
                .build();
    }

    private static ProductSellingStatus determineSellingStatus(Integer quantity) {
        if (quantity != null && quantity == 0) {
            return SOLD_OUT;
        }
        return SELLING;
    }

    private static void validate(ProductCreate productCreate, Store store) {
        if (productCreate.getName() == null || productCreate.getName().isBlank()) {
            throw new ProductException("상품 이름은 필수 입력 값입니다.");
        }
        if (productCreate.getPrice() <= 0) {
            throw new ProductException("상품 가격은 양수여야 합니다.");
        }
        if (productCreate.getType() == null) {
            throw new ProductException("상품 타입은 필수 입력 값입니다.");
        }
        if (!store.getName().equals(productCreate.getStoreName())) {
            throw new ProductException("가게가 일치하지 않습니다.");
        }
    }
}
