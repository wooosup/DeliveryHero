package hello.delivery.user.controller.request;

import hello.delivery.common.domain.Address;
import hello.delivery.user.service.port.in.SignupCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Signup(
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(max = 4, message = "이름은 최대 4자까지 입력 가능합니다.")
        String name,

        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Size(min = 5, max = 20, message = "아이디는 5자 이상 20자 이하로 입력 가능합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력 가능합니다.")
        String password,

        @NotBlank(message = "주소는 필수 입력 값입니다.")
        String address
) {

    public SignupCommand toCommand() {
        return SignupCommand.of(name, username, password, Address.of(address));
    }

}