package hello.delivery.common.service.port;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface ClockHolder {

    LocalDate now();

    LocalDateTime nowDateTime();

    LocalTime nowTime();
}
