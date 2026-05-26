package hello.delivery.store.domain;

import hello.delivery.common.exception.StoreException;
import java.time.LocalDate;
import java.time.LocalTime;

public record StoreRegistration(
        String name,
        StoreType type,
        LocalTime openTime,
        LocalTime closeTime,
        LocalDate openDate
) {

    public StoreRegistration {
        if (name == null || name.isBlank()) {
            throw new StoreException("가게 이름은 필수 입력 값입니다.");
        }
        if (type == null) {
            throw new StoreException("가게 타입은 필수 입력 값입니다.");
        }
        if (openTime == null) {
            throw new StoreException("오픈 시간은 필수 입력 값입니다.");
        }
        if (closeTime == null) {
            throw new StoreException("마감 시간은 필수 입력 값입니다.");
        }
        if (openDate == null) {
            throw new StoreException("오픈 날짜는 필수 입력 값입니다.");
        }
    }

}
