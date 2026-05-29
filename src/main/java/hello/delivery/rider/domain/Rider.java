package hello.delivery.rider.domain;

import hello.delivery.common.exception.RiderException;
import lombok.Builder;
import lombok.Getter;

import static hello.delivery.rider.domain.RiderStatus.*;

@Getter
public class Rider {

    private final Long id;
    private final String name;
    private final String phone;
    private final RiderStatus status;

    @Builder
    private Rider(Long id, String name, String phone, RiderStatus status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public static Rider signup(RiderRegistration registration) {
        return Rider.builder()
                .name(registration.name())
                .phone(registration.phone())
                .status(OFFLINE)
                .build();
    }

    public Rider login() {
        return Rider.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .status(AVAILABLE)
                .build();
    }

    public Rider changeStatus(RiderStatus newStatus) {
        if (newStatus == null) {
            throw new RiderException("라이더 상태는 필수입니다.");
        }
        return Rider.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .status(newStatus)
                .build();
    }

    public void validateAvailable() {
        if (status != AVAILABLE) {
            throw new RiderException("배달을 수행할 수 없습니다.");
        }
    }

    public void validateCanStartDelivery() {
        if (status != ASSIGNED) {
            throw new RiderException("배달을 시작할 수 없는 상태입니다. (현재 상태: " + status.getDescription() + ")");
        }
    }

    public void validateCanCompleteDelivery() {
        if (status != DELIVERING) {
            throw new RiderException("배달 중이 아닌 상태에서는 완료할 수 없습니다.");
        }
    }
}
