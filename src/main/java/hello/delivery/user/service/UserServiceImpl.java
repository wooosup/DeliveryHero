package hello.delivery.user.service;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;

import hello.delivery.common.exception.UserException;
import hello.delivery.common.exception.UserNotFound;
import hello.delivery.user.controller.port.UserService;
import hello.delivery.user.domain.AddressUpdate;
import hello.delivery.user.domain.Login;
import hello.delivery.user.domain.PasswordUpdate;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;
import hello.delivery.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User signupCustomer(UserCreate userCreate) {
        validateUsernameNotExists(userCreate.getUsername());
        User user = User.signup(userCreate, CUSTOMER);
        return userRepository.save(user);
    }

    public User signupOwner(UserCreate userCreate) {
        validateUsernameNotExists(userCreate.getUsername());
        User user = User.signup(userCreate, OWNER);
        return userRepository.save(user);
    }

    public User login(Login login) {
        User user = userRepository.findByUsername(login.getUsername())
                .orElseThrow(UserNotFound::new);
        user.checkPassword(login.getPassword());

        return user;
    }

    public User changeAddress(Long userId, AddressUpdate addressUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        user = user.changeAddress(addressUpdate.getAddress());

        return userRepository.save(user);
    }

    public User changePassword(Long userId, PasswordUpdate passwordUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        user = user.changePassword(passwordUpdate.getPassword());

        return userRepository.save(user);
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserException("이미 존재하는 아이디입니다.");
        }
    }
}
