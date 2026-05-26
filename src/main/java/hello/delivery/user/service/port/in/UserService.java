package hello.delivery.user.service.port.in;

import hello.delivery.user.domain.User;

public interface UserService {

    User signupCustomer(SignupCommand command);

    User signupOwner(SignupCommand command);

    User login(LoginCommand command);

    User changeAddress(Long userId, AddressUpdateCommand command);

    User changePassword(Long userId, PasswordUpdateCommand passwordUpdate);

}
