package hello.delivery.rider.controller.request;

import hello.delivery.rider.service.port.in.RiderCreateCommand;
import jakarta.validation.constraints.NotBlank;

public record RiderCreate(
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        String name,

        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        String phone
) {

    public RiderCreateCommand toCommand() {
        return RiderCreateCommand.of(name, phone);
    }

}
