package hello.delivery.order.infrastructure;

import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.delivery.domain.DeliveryAddress;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderStatus;
import hello.delivery.store.infrastructure.StoreEntity;
import hello.delivery.user.infrastructure.UserEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreEntity store;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "delivery_address"))
    private DeliveryAddress address;

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
        List<OrderProductEntity> children = OrderProductEntity.of(order.getOrderProducts(), orderEntity);
        orderEntity.orderProducts.addAll(children);
        orderEntity.orderStatus = order.getOrderStatus();
        return orderEntity;
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