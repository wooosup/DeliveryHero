package hello.delivery.user.domain;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;

public record UserRegistration(
        String name,
        String username,
        String encodedPassword,
        Address address,
        UserRole role
) {

    public UserRegistration {
        if (name == null || name.isBlank()) {
            throw new UserException("이름은 필수입니다.");
        }
        if (username == null || username.isBlank()) {
            throw new UserException("아이디는 필수입니다.");
        }
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new UserException("비밀번호는 필수입니다.");
        }
        validateAddress(address);
        if (role == null) {
            throw new UserException("권한은 필수입니다.");
        }
    }

    private static void validateAddress(Address address) {
        if (address == null || address.getAddress() == null || address.getAddress().isBlank()) {
            throw new UserException("주소는 비어 있을 수 없습니다.");
        }
    }

}
