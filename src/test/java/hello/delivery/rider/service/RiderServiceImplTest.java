package hello.delivery.rider.service;

import static hello.delivery.rider.domain.RiderStatus.AVAILABLE;
import static hello.delivery.rider.domain.RiderStatus.DELIVERING;
import static hello.delivery.rider.domain.RiderStatus.OFFLINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.delivery.common.exception.RiderException;
import hello.delivery.common.exception.RiderNotFound;
import hello.delivery.mock.FakeRiderRepository;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.service.port.in.RiderCreateCommand;
import hello.delivery.rider.service.port.in.RiderLoginCommand;
import hello.delivery.rider.service.port.in.RiderService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RiderServiceImplTest {

    private RiderService riderService;

    @BeforeEach
    void setUp() {
        FakeRiderRepository fakeRiderRepository = new FakeRiderRepository();
        riderService = new RiderServiceImpl(fakeRiderRepository);
    }

    @Test
    @DisplayName("라이더는 회원가입을 할 수 있다")
    void signup() {
        // given
        RiderCreateCommand command = RiderCreateCommand.of("홍길동", "010-1234-5678");

        // when
        Rider result = riderService.signup(command);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(OFFLINE);
    }

    @Test
    @DisplayName("같은 전화번호로 회원가입을 하면 예외를 던진다")
    void validatePhone() {
        // given
        RiderCreateCommand command = RiderCreateCommand.of("홍길동", "010-1234-5678");
        riderService.signup(command);

        // expect
        assertThatThrownBy(() -> riderService.signup(command))
                .isInstanceOf(RiderException.class)
                .hasMessageContaining("이미 등록된 전화번호입니다.");
    }

    @Test
    @DisplayName("라이더는 로그인할 수 있다")
    void login() {
        // given
        riderService.signup(RiderCreateCommand.of("홍길동", "010-1234-5678"));

        // when
        Rider result = riderService.login(RiderLoginCommand.of("010-1234-5678"));

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(AVAILABLE);
    }

    @Test
    @DisplayName("존재하지 않는 라이더가 로그인하면 예외를 던진다")
    void validateLogin() {
        // expect
        assertThatThrownBy(() -> riderService.login(RiderLoginCommand.of("010-1234-5678")))
                .isInstanceOf(RiderNotFound.class)
                .hasMessageContaining("라이더를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("라이더의 상태를 변경할 수 있다")
    void changeStatus() {
        // given
        Rider rider = riderService.signup(RiderCreateCommand.of("홍길동", "010-1234-5678"));

        // when
        Rider result = riderService.changeStatus(rider.getId(), DELIVERING);

        // then
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        assertThat(result.getStatus()).isEqualTo(DELIVERING);
    }

    @Test
    @DisplayName("배달 가능한 라이더를 조회할 수 있다")
    void findAvailableRiders() {
        // given
        riderService.signup(RiderCreateCommand.of("홍길동", "010-1234-5678"));
        riderService.signup(RiderCreateCommand.of("김길동", "010-9876-5432"));

        riderService.login(RiderLoginCommand.of("010-1234-5678"));
        riderService.login(RiderLoginCommand.of("010-9876-5432"));

        // when
        List<Rider> riders = riderService.findAvailableRiders();

        // then
        assertThat(riders).hasSize(2);
        assertThat(riders).extracting("phone").containsExactlyInAnyOrder("010-1234-5678", "010-9876-5432");
    }

}
