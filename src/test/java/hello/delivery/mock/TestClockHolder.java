package hello.delivery.mock;

import hello.delivery.common.service.port.ClockHolder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestClockHolder implements ClockHolder {

    private final LocalDateTime fixedDateTime = LocalDateTime.of(2025, 11, 23, 12, 30, 0);

    @Override
    public LocalDate now() {
        return fixedDateTime.toLocalDate();
    }

    @Override
    public LocalDateTime nowDateTime() {
        return fixedDateTime;
    }

    @Override
    public LocalTime nowTime() {
        return fixedDateTime.toLocalTime();
    }
}
