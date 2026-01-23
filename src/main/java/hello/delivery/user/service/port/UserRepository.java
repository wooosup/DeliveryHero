package hello.delivery.user.service.port;

import hello.delivery.user.domain.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
