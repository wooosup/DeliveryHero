package hello.delivery.order.infrastructure;

import hello.delivery.common.domain.Money;
import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.order.domain.OrderProduct;
import hello.delivery.product.infrastructure.ProductEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    private int quantity;

    @Builder
    private OrderProductEntity(OrderEntity order, ProductEntity product, Money price, int quantity) {
        this.order = order;
        this.product = product;
        this.price = price == null ? Money.zero() : price;
        this.quantity = quantity;
    }

    public static OrderProductEntity of(OrderProduct orderProduct) {
        return OrderProductEntity.builder()
                .product(ProductEntity.of(orderProduct.getProduct()))
                .price(orderProduct.getPrice())
                .quantity(orderProduct.getQuantity())
                .build();
    }

    void assignOrder(OrderEntity order) {
        this.order = order;
    }

    public OrderProduct toDomain() {
        return OrderProduct.builder()
                .id(id)
                .product(product.toDomain())
                .price(price)
                .quantity(quantity)
                .build();
    }

}
