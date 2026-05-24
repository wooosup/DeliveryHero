package hello.delivery.user.service.port.in;

import hello.delivery.user.domain.AddressUpdate;
import hello.delivery.user.domain.Login;
import hello.delivery.user.domain.PasswordUpdate;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;

public interface UserService {

    User signupCustomer(UserCreate userCreate);

    User signupOwner(UserCreate userCreate);

    User login(Login login);

    User changeAddress(Long userId, AddressUpdate addressUpdate);

    User changePassword(Long userId, PasswordUpdate passwordUpdate);

}
