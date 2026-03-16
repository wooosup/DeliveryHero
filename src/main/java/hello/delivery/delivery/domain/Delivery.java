package hello.delivery.delivery.domain;

import static hello.delivery.delivery.domain.DeliveryStatus.*;
import static hello.delivery.order.domain.OrderStatus.ACCEPTED;

import hello.delivery.common.exception.DeliveryException;
import hello.delivery.common.service.port.ClockHolder;
import hello.delivery.order.domain.Order;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Delivery {

    private final Long id;
    private final Long orderId;
    private final Long riderId;
    private final DeliveryStatus status;
    private final DeliveryAddress address;
    private final LocalDateTime startedAt;
    private final LocalDateTime completedAt;

    @Builder
    private Delivery(Long id, Long orderId, Long riderId, DeliveryStatus status, DeliveryAddress address,
                     LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.orderId = orderId;
        this.riderId = riderId;
        this.status = status;
        this.address = address;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public static Delivery create(Order order) {
        validate(order);
        return Delivery.builder()
                .orderId(order.getId())
                .status(PENDING)
                .address(order.getAddress())
                .build();
    }

    public Delivery assign(Long riderId) {
        if (status != PENDING) {
            throw new DeliveryException("배차 가능한 상태가 아닙니다.");
        }
        return Delivery.builder()
                .id(id)
                .orderId(orderId)
                .riderId(riderId)
                .status(ASSIGNED)
                .address(address)
                .build();
    }

    public Delivery start(Long riderId, ClockHolder clockHolder) {
        validateRider(riderId);
        if (status != ASSIGNED) {
            throw new DeliveryException("배달을 시작할 수 없는 상태입니다.");
        }
        return Delivery.builder()
                .id(id)
                .orderId(orderId)
                .riderId(riderId)
                .status(PICKED_UP)
                .address(address)
                .startedAt(clockHolder.nowDateTime())
                .build();
    }

    public Delivery complete(Long riderId, ClockHolder clockHolder) {
        validateRider(riderId);
        if (status != PICKED_UP) {
            throw new DeliveryException("배달을 완료할 수 없는 상태입니다.");
        }
        return Delivery.builder()
                .id(id)
                .orderId(orderId)
                .riderId(riderId)
                .status(DELIVERED)
                .address(address)
                .startedAt(startedAt)
                .completedAt(clockHolder.nowDateTime())
                .build();
    }

    private void validateRider(Long riderId) {
        if (!this.riderId.equals(riderId)) {
            throw new DeliveryException("해당 배달의 담당 라이더가 아닙니다.");
        }
    }

    private static void validate(Order order) {
        if (order == null) {
            throw new DeliveryException("배달에 필요한 주문 정보가 없습니다.");
        }
        if (order.getOrderStatus() != ACCEPTED) {
            throw new DeliveryException("수락된 주문만 배달을 생성할 수 있습니다.");
        }
        if (order.getAddress() == null) {
            throw new DeliveryException("배달에 필요한 주소 정보가 없습니다.");
        }
    }

}
