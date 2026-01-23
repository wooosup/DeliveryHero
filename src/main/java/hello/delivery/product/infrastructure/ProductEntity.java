package hello.delivery.product.infrastructure;

import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.domain.Stock;
import hello.delivery.store.infrastructure.StoreEntity;
import hello.delivery.user.infrastructure.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    private String name;

    private int price;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    private ProductSellingStatus productSellingStatus;

    private Integer stock;

    @Builder
    private ProductEntity(Long id, StoreEntity store, UserEntity owner, String name, int price, ProductType productType,
                          ProductSellingStatus productSellingStatus, Integer stock) {
        this.id = id;
        this.store = store;
        this.owner = owner;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.productSellingStatus = productSellingStatus;
        this.stock = stock;
    }

    public static ProductEntity of(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .store(StoreEntity.of(product.getStore()))
                .owner(UserEntity.of(product.getOwner()))
                .name(product.getName())
                .price(product.getPrice())
                .productType(product.getProductType())
                .productSellingStatus(product.getProductSellingStatus())
                .stock(product.getStock() == null ? null : product.getStock().getQuantity())
                .build();
    }

    public Product toDomain() {
        return Product.builder()
                .id(id)
                .store(store.toDomain())
                .owner(owner.toDomain())
                .name(name)
                .price(price)
                .productType(productType)
                .productSellingStatus(productSellingStatus)
                .stock(stock == null ? null : Stock.of(stock))
                .build();
    }
}
