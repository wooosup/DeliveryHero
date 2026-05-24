package hello.delivery.delivery.infrastructure;

import hello.delivery.common.infrastructure.BaseEntity;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.common.domain.Address;
import hello.delivery.delivery.domain.DeliveryStatus;
import hello.delivery.order.infrastructure.OrderEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    private Long riderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "delivery_address"))
    private Address address;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Builder
    private DeliveryEntity(Long id, OrderEntity order, Long riderId, DeliveryStatus status, Address address,
                           LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.order = order;
        this.riderId = riderId;
        this.status = status;
        this.address = address;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static DeliveryEntity of(Delivery delivery, OrderEntity orderEntity) {
        return DeliveryEntity.builder()
                .id(delivery.getId())
                .order(orderEntity)
                .riderId(delivery.getRiderId())
                .status(delivery.getStatus())
                .address(delivery.getAddress())
                .startedAt(delivery.getStartedAt())
                .completedAt(delivery.getCompletedAt())
                .build();
    }

    public Delivery toDomain() {
        return Delivery.builder()
                .id(id)
                .orderId(order.getId())
                .riderId(riderId)
                .status(status)
                .address(address)
                .startedAt(startedAt)
                .completedAt(completedAt)
                .build();
    }
}

