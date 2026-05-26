package hello.delivery.rider.controller.request;

import hello.delivery.rider.service.port.in.RiderLoginCommand;
import jakarta.validation.constraints.NotBlank;

public record RiderLogin(
        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        String phone
) {

    public RiderLoginCommand toCommand() {
        return RiderLoginCommand.of(phone);
    }

}
