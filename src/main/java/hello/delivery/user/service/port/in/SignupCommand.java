package hello.delivery.user.service.port.in;

import hello.delivery.common.domain.Address;
import hello.delivery.user.domain.UserRegistration;
import hello.delivery.user.domain.UserRole;

public record SignupCommand(
        String name,
        String username,
        String password,
        Address address
) {

    public static SignupCommand of(String name, String username, String password, Address address) {
        return new SignupCommand(name, username, password, address);
    }

    public UserRegistration toRegistration(String encodedPassword, UserRole role) {
        return new UserRegistration(name, username, encodedPassword, address, role);
    }

}

