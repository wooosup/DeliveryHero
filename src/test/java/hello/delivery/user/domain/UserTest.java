package hello.delivery.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final String RAW_PASSWORD = "hihihi3454";
    private static final String ENCODED_PASSWORD = "$2a$10$123456789012345678901u9s9T2hYj2S9lYj2S9lYj2S9lYj2S9lY";

    @Test
    @DisplayName("회원가입 시 서비스가 전달한 암호화 비밀번호를 저장한다.")
    void signupStoresEncodedPassword() {
        User signupUser = User.signup("wss", "wss3325", ENCODED_PASSWORD, Address.of("Daegu"), UserRole.CUSTOMER);

        assertThat(signupUser.getName()).isEqualTo("wss");
        assertThat(signupUser.getUsername()).isEqualTo("wss3325");
        assertThat(signupUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(signupUser.getPassword()).isNotEqualTo(RAW_PASSWORD);
        assertThat(signupUser.getAddress().getAddress()).isEqualTo("Daegu");
    }

    @Test
    @DisplayName("주소 변경 시 암호화 비밀번호를 유지한다.")
    void changeAddressKeepsEncodedPassword() {
        User user = User.builder()
                .name("wss")
                .username("wss3325")
                .password(ENCODED_PASSWORD)
                .address(Address.of("Daegu"))
                .role(UserRole.CUSTOMER)
                .build();

        User changedUser = user.changeAddress(Address.of("Seoul"));

        assertThat(changedUser.getAddress().getAddress()).isEqualTo("Seoul");
        assertThat(changedUser.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    @Test
    @DisplayName("너무 짧은 비밀번호는 검증에서 예외를 던진다.")
    void validatePasswordRejectsTooShortPasswords() {
        assertThatThrownBy(() -> User.validatePassword("1234"))
                .isInstanceOf(UserException.class);
    }
}
