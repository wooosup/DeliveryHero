package hello.delivery.user.service;

import hello.delivery.common.exception.UserNotFound;
import hello.delivery.user.domain.User;
import hello.delivery.user.service.port.out.UserFinder;
import hello.delivery.user.service.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinderImpl implements UserFinder {

    private final UserRepository userRepository;

    @Override
    public User findByUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFound::new);
    }

}
