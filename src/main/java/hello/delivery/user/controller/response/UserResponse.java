package hello.delivery.user.controller.response;

import hello.delivery.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String name;
    private final String username;
    private final String address;

    @Builder
    private UserResponse(Long id, String name, String username, String address) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.address = address;
    }

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .address(user.getAddress().getAddress())
                .build();
    }
}
