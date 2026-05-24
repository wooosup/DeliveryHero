package hello.delivery.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    @DisplayName("금액을 더할 수 있다.")
    void plus() {
        // given
        Money money = Money.of(1000);

        // when
        Money result = money.plus(Money.of(2000));

        // then
        assertThat(result).isEqualTo(Money.of(3000));
    }

    @Test
    @DisplayName("금액에 배수를 곱할 수 있다.")
    void multiply() {
        // given
        Money money = Money.of(3000);

        // when
        Money result = money.multiply(3);

        // then
        assertThat(result).isEqualTo(Money.of(9000));
    }

    @Test
    @DisplayName("금액은 음수일 수 없다.")
    void validateAmount() {
        assertThatThrownBy(() -> Money.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("금액은 음수일 수 없습니다.");
    }

}
