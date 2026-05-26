package hello.delivery.user.service;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import hello.delivery.common.exception.UserNotFound;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserRole;
import hello.delivery.user.service.port.in.AddressUpdateCommand;
import hello.delivery.user.service.port.in.LoginCommand;
import hello.delivery.user.service.port.in.PasswordUpdateCommand;
import hello.delivery.user.service.port.in.SignupCommand;
import hello.delivery.user.service.port.in.UserService;
import hello.delivery.user.service.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signupCustomer(SignupCommand command) {
        return signup(command, CUSTOMER);
    }

    public User signupOwner(SignupCommand command) {
        return signup(command, OWNER);
    }

    public User login(LoginCommand login) {
        User user = userRepository.findByUsername(login.username())
                .orElseThrow(UserNotFound::new);
        checkPassword(login, user);

        return user;
    }

    public User changeAddress(Long userId, AddressUpdateCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        user = user.changeAddress(Address.of(command.address()));

        return userRepository.save(user);
    }

    public User changePassword(Long userId, PasswordUpdateCommand passwordUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        String rawPassword = passwordUpdate.password();
        User.validatePassword(rawPassword);

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserException("이전 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        user = user.changeEncodedPassword(encodedPassword);

        return userRepository.save(user);
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserException("이미 존재하는 아이디입니다.");
        }
    }

    private void checkPassword(LoginCommand login, User user) {
        if (!passwordEncoder.matches(login.password(), user.getPassword())) {
            throw new UserException("비밀번호가 일치하지 않습니다.");
        }
    }

    private User signup(SignupCommand command, UserRole role) {
        validateUsernameNotExists(command.username());
        User.validatePassword(command.password());
        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.signup(command.name(), command.username(), encodedPassword, command.address(), role);

        return userRepository.save(user);
    }

}
