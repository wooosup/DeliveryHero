package hello.delivery.rider.domain;

import hello.delivery.common.exception.RiderException;

public record RiderRegistration(
        String name,
        String phone
) {

    public RiderRegistration {
        if (name == null || name.isBlank()) {
            throw new RiderException("이름은 필수입니다.");
        }
        if (phone == null || phone.isBlank()) {
            throw new RiderException("전화번호는 필수입니다.");
        }
    }

}
