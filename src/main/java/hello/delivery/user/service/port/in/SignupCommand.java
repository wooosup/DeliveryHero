package hello.delivery.user.service.port.in;

import hello.delivery.common.domain.Address;

public record SignupCommand(
        String name,
        String username,
        String password,
        Address address
) {

    public static SignupCommand of(String name, String username, String password, Address address) {
        return new SignupCommand(name, username, password, address);
    }

}

