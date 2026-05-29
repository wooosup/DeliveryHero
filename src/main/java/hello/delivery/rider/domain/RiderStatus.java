package hello.delivery.rider.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RiderStatus {

    OFFLINE("업무 종료"),
    AVAILABLE("배차 대기 중"),
    ASSIGNED("배차 완료"),
    DELIVERING("배달 중");

    private final String description;
}
