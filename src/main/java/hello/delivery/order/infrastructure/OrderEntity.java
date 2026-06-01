package hello.delivery.order.infrastructure;

import hello.delivery.common.domain.Address;
import hello.delivery.common.domain.Money;
import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderStatus;
import hello.delivery.store.infrastructure.StoreEntity;
import hello.delivery.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_price"))
    private Money totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreEntity store;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "delivery_address"))
    private Address address;

    private LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderProductEntity> orderProducts = new ArrayList<>();

    public static OrderEntity of(Order order) {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.id = order.getId();
        orderEntity.totalPrice = order.getTotalPrice();
        orderEntity.user = UserEntity.of(order.getUser());
        orderEntity.store = StoreEntity.of(order.getStore());
        orderEntity.address = order.getAddress();
        orderEntity.orderedAt = order.getOrderedAt();
        orderEntity.orderStatus = order.getOrderStatus();

        order.getOrderProducts().stream()
                .map(OrderProductEntity::of)
                .forEach(orderEntity::addOrderProduct);

        return orderEntity;
    }

    private void addOrderProduct(OrderProductEntity orderProduct) {
        Objects.requireNonNull(orderProduct, "주문 상품은 필수입니다.");
        this.orderProducts.add(orderProduct);
        orderProduct.assignOrder(this);
    }

    public Order toDomain() {
        return Order.builder()
                .id(id)
                .user(user.toDomain())
                .store(store.toDomain())
                .address(address)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts.stream()
                        .map(OrderProductEntity::toDomain)
                        .toList())
                .orderStatus(orderStatus)
                .build();
    }
}
