package hello.delivery.user.domain;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String name;
    private final String username;
    private final String password;
    private final Address address;
    private final UserRole role;

    @Builder
    private User(Long id, String name, String username, String password, Address address, UserRole role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.role = role;
    }

    public static User signup(UserCreate userCreate, UserRole role, String encodedPassword) {
        validate(userCreate);
        return User.builder()
                .name(userCreate.getName())
                .username(userCreate.getUsername())
                .password(encodedPassword)
                .address(Address.of(userCreate.getAddress()))
                .role(role)
                .build();
    }

    public User changeAddress(Address newAddress) {
        validateAddress(newAddress);
        return User.builder()
                .id(id)
                .name(name)
                .username(username)
                .password(password)
                .address(newAddress)
                .role(role)
                .build();
    }

    public User changeEncodedPassword(String encodedPassword) {
        return User.builder()
                .id(id)
                .name(name)
                .username(username)
                .password(encodedPassword)
                .address(address)
                .role(role)
                .build();
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new UserException("비밀번호는 필수입니다.");
        }
        validatePasswordLength(password);
    }

    public boolean isNotOwner(Long anotherId) {
        return !this.id.equals(anotherId);
    }

    public void validateOwnerRole() {
        if (isNotRoleOfOwner()) {
            throw new UserException("권한이 없습니다.");
        }
    }

    private boolean isNotRoleOfOwner() {
        return this.role != UserRole.OWNER;
    }

    private static void validate(UserCreate userCreate) {
        if (userCreate.getName() == null || userCreate.getName().isBlank()) {
            throw new UserException("이름은 필수입니다.");
        }
        if (userCreate.getUsername() == null || userCreate.getUsername().isBlank()) {
            throw new UserException("아이디는 필수입니다.");
        }
        if (userCreate.getPassword() == null || userCreate.getPassword().isBlank()) {
            throw new UserException("비밀번호는 필수입니다.");
        }
        if (userCreate.getAddress() == null || userCreate.getAddress().isBlank()) {
            throw new UserException("주소는 필수입니다.");
        }
        validateUsernameLength(userCreate.getUsername());
        validatePasswordLength(userCreate.getPassword());
    }

    private void validateAddress(Address newAddress) {
        if (newAddress == null) {
            throw new UserException("주소는 비어 있을 수 없습니다.");
        }
    }

    private static void validatePasswordLength(String password) {
        if (password.length() < 8 || password.length() > 20) {
            throw new UserException("비밀번호는 8자 이상 20자 이하로 입력 가능합니다.");
        }
    }

    private static void validateUsernameLength(String username) {
        if (username.length() < 5 || username.length() > 20) {
            throw new UserException("아이디는 5자 이상 20자 이하로 입력 가능합니다.");
        }
    }

}
