package hello.delivery.mock;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;

import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {

    private final AtomicLong autoIncrement = new AtomicLong(1);
    private final List<User> data = new ArrayList<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            User newUser = User.builder()
                    .id(autoIncrement.getAndIncrement())
                    .name(user.getName())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .address(user.getAddress())
                    .role(user.getRole())
                    .build();
            data.add(newUser);
            return newUser;
        } else {
            data.removeIf(u -> u.getId().equals(user.getId()));
            data.add(user);
            return user;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.stream()
                .filter(u -> u.getUsername().equals(username))
                .findAny();
    }

    @Override
    public boolean existsByUsername(String username) {
        return data.stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

}
