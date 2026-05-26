package hello.delivery.user.controller.request;

import hello.delivery.user.service.port.in.AddressUpdateCommand;
import jakarta.validation.constraints.NotBlank;

public record AddressUpdate(
        @NotBlank(message = "주소는 필수 입력 값입니다.")
        String address
) {

    public AddressUpdateCommand toCommand() {
        return AddressUpdateCommand.from(address);
    }

}
