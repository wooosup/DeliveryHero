package hello.delivery.user.service.port.out;

import hello.delivery.user.domain.User;

public interface UserFinder {

    User findByUser(Long id);

}
