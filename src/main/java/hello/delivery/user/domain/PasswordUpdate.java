package hello.delivery.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordUpdate {

    @NotNull(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력 가능합니다.")
    private final String password;

    @Builder
    private PasswordUpdate(@JsonProperty("password") String password) {
        this.password = password;
    }

}
