package hello.delivery.user.controller.request;

import hello.delivery.user.service.port.in.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record Login(
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        String username,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password
) {

    public LoginCommand toCommand() {
        return LoginCommand.of(username, password);
    }

}
