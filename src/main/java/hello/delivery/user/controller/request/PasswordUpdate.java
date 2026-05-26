package hello.delivery.user.controller.request;

import hello.delivery.user.service.port.in.PasswordUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordUpdate(
        @NotNull(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력 가능합니다.")
        String password
) {

    public PasswordUpdateCommand toCommand() {
        return PasswordUpdateCommand.from(password);
    }

}
