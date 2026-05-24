package hello.delivery.user.service;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import hello.delivery.common.exception.UserNotFound;
import hello.delivery.user.service.port.in.UserService;
import hello.delivery.user.domain.AddressUpdate;
import hello.delivery.user.domain.Login;
import hello.delivery.user.domain.PasswordUpdate;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;
import hello.delivery.user.domain.UserRole;
import hello.delivery.user.service.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signupCustomer(UserCreate userCreate) {
        return signup(userCreate, CUSTOMER);
    }

    public User signupOwner(UserCreate userCreate) {
        return signup(userCreate, OWNER);
    }

    public User login(Login login) {
        User user = userRepository.findByUsername(login.getUsername())
                .orElseThrow(UserNotFound::new);
        checkPassword(login, user);

        return user;
    }

    public User changeAddress(Long userId, AddressUpdate addressUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        user = user.changeAddress(Address.of(addressUpdate.getAddress()));

        return userRepository.save(user);
    }

    public User changePassword(Long userId, PasswordUpdate passwordUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        String rawPassword = passwordUpdate.getPassword();
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

    private void checkPassword(Login login, User user) {
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new UserException("비밀번호가 일치하지 않습니다.");
        }
    }

    private User signup(UserCreate userCreate, UserRole role) {
        validateUsernameNotExists(userCreate.getUsername());
        String encodedPassword = passwordEncoder.encode(userCreate.getPassword());
        User user = User.signup(userCreate, role, encodedPassword);
        return userRepository.save(user);
    }

}
