package hello.delivery.user.infrastructure;

import hello.delivery.common.domain.Address;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserRole;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String password;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    private UserEntity(Long id, String name, String username, String password, Address address, UserRole role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.role = role;
    }

    public static UserEntity of(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .password(user.getPassword())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .id(id)
                .name(name)
                .username(username)
                .password(password)
                .address(address)
                .role(role)
                .build();
    }
}
