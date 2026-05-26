package hello.delivery.user.service;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import hello.delivery.common.exception.UserNotFound;
import hello.delivery.mock.FakeUserRepository;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.in.AddressUpdateCommand;
import hello.delivery.user.service.port.in.LoginCommand;
import hello.delivery.user.service.port.in.PasswordUpdateCommand;
import hello.delivery.user.service.port.in.SignupCommand;
import hello.delivery.user.service.port.in.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceImplTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_NAME = "Wss";
    private static final String DEFAULT_USERNAME = "wss3325";
    private static final String DEFAULT_PASSWORD = "hihihi3454";
    private static final String DEFAULT_ADDRESS = "Daegu";
    private static final String NEW_ADDRESS = "Seoul";
    private static final String NEW_PASSWORD = "hihihi9999";
    private static final String INVALID_SHORT_PASSWORD = "9999";

    @BeforeEach
    void setUp() {
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(fakeUserRepository, passwordEncoder);
    }

    @Test
    @DisplayName("고객 회원가입 시 암호화된 비밀번호를 저장한다.")
    void signupCustomer() {
        User result = userService.signupCustomer(createSignupCommand());

        assertThat(result.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(result.getAddress().getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(result.getRole()).isEqualTo(CUSTOMER);
        assertThat(result.getPassword()).isNotEqualTo(DEFAULT_PASSWORD);
        assertThat(passwordEncoder.matches(DEFAULT_PASSWORD, result.getPassword())).isTrue();
    }

    @Test
    @DisplayName("사장 회원가입 시 암호화된 비밀번호를 저장한다.")
    void signupOwner() {
        User result = userService.signupOwner(createSignupCommand());

        assertThat(result.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(result.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(result.getAddress().getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(result.getRole()).isEqualTo(OWNER);
        assertThat(result.getPassword()).isNotEqualTo(DEFAULT_PASSWORD);
        assertThat(passwordEncoder.matches(DEFAULT_PASSWORD, result.getPassword())).isTrue();
    }

    @Test
    @DisplayName("원문 비밀번호로 로그인할 수 있다.")
    void login() {
        userService.signupCustomer(createSignupCommand());
        LoginCommand loginRequest = createLoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        User result = userService.login(loginRequest);

        assertThat(result.getUsername()).isEqualTo(DEFAULT_USERNAME);
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 예외를 던진다.")
    void invalidLogin() {
        userService.signupCustomer(createSignupCommand());
        LoginCommand loginRequest = createLoginRequest("zzzzzzz", "hihihi1111");

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외를 던진다.")
    void invalidLoginWrong() {
        userService.signupCustomer(createSignupCommand());
        LoginCommand loginRequest = createLoginRequest(DEFAULT_USERNAME, "hihihi1111");

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("주소 변경 시 주소만 변경된다.")
    void changeAddress() {
        User user = userService.signupCustomer(createSignupCommand());
        AddressUpdateCommand addressUpdate = createAddressUpdate(NEW_ADDRESS);

        User result = userService.changeAddress(user.getId(), addressUpdate);

        assertThat(result.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(result.getAddress().getAddress()).isEqualTo(NEW_ADDRESS);
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("비밀번호 변경 시 새 암호화 비밀번호를 저장하고 로그인할 수 있다.")
    void changePassword() {
        User user = userService.signupCustomer(createSignupCommand());
        PasswordUpdateCommand passwordUpdate = createPasswordUpdate(NEW_PASSWORD);

        User changedUser = userService.changePassword(user.getId(), passwordUpdate);
        User loginUser = userService.login(createLoginRequest(DEFAULT_USERNAME, NEW_PASSWORD));

        assertThat(loginUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(changedUser.getPassword()).isNotEqualTo(NEW_PASSWORD);
        assertThat(passwordEncoder.matches(NEW_PASSWORD, changedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("너무 짧은 비밀번호로 변경하면 예외를 던진다.")
    void validateLengthChangePassword() {
        User user = userService.signupCustomer(createSignupCommand());
        PasswordUpdateCommand passwordUpdate = createPasswordUpdate(INVALID_SHORT_PASSWORD);

        assertThatThrownBy(() -> userService.changePassword(user.getId(), passwordUpdate))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("현재 비밀번호와 같은 비밀번호로 변경하면 예외를 던진다.")
    void rejectCurrentPassword() {
        User user = userService.signupCustomer(createSignupCommand());
        PasswordUpdateCommand passwordUpdate = createPasswordUpdate(DEFAULT_PASSWORD);

        assertThatThrownBy(() -> userService.changePassword(user.getId(), passwordUpdate))
                .isInstanceOf(UserException.class);
    }

    private SignupCommand createSignupCommand() {
        return SignupCommand.of(
                DEFAULT_NAME,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD,
                Address.of(DEFAULT_ADDRESS)
        );
    }

    private LoginCommand createLoginRequest(String username, String password) {
        return LoginCommand.of(username, password);
    }

    private AddressUpdateCommand createAddressUpdate(String address) {
        return AddressUpdateCommand.from(address);
    }

    private PasswordUpdateCommand createPasswordUpdate(String password) {
        return PasswordUpdateCommand.from(password);
    }
}
