package hello.delivery.rider.domain;

import static hello.delivery.rider.domain.RiderStatus.AVAILABLE;
import static hello.delivery.rider.domain.RiderStatus.DELIVERING;
import static hello.delivery.rider.domain.RiderStatus.OFFLINE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RiderTest {

    @Test
    @DisplayName("라이더를 회원가입할 수 있다")
    void signup() {
        // given
        RiderRegistration registration = new RiderRegistration("홍길동", "010-1234-5678");

        // when
        Rider result = Rider.signup(registration);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(OFFLINE);
    }

    @Test
    @DisplayName("라이더가 로그인하면 배달 가능 상태가 된다")
    void login() {
        // given
        Rider rider = Rider.signup(new RiderRegistration("홍길동", "010-1234-5678"));

        // when
        Rider result = rider.login();

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(AVAILABLE);
    }

    @Test
    @DisplayName("라이더 상태를 변경할 수 있다")
    void changeStatus() {
        // given
        Rider rider = Rider.signup(new RiderRegistration("홍길동", "010-1234-5678"));

        // when
        Rider result = rider.changeStatus(DELIVERING);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(DELIVERING);
    }

}
