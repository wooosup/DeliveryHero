package hello.delivery.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("대기 중"),
    ACCEPTED("사장님 수락"),
    CANCELLED("주문 취소"),
    COMPLETED("배달 완료");

    private final String description;

}
