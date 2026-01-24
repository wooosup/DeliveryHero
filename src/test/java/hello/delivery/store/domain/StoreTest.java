package hello.delivery.store.domain;

import static hello.delivery.store.domain.StoreType.KOREAN_FOOD;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;

import hello.delivery.mock.TestClockHolder;
import hello.delivery.user.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreTest {

    @Test
    @DisplayName("owner와 StoreCreate로 Store를 생성한다.")
    void create() throws Exception {
        // given
        LocalDate now = new TestClockHolder().now();
        User owner = buildOwner();
        LocalTime openTime = LocalTime.of(12, 0);
        LocalTime closeTIme = LocalTime.of(21, 0);

        StoreCreate storeCreate = StoreCreate.builder()
                .storeType(KOREAN_FOOD)
                .storeName("한식당")
                .openTime(openTime)
                .closeTime(closeTIme)
                .build();

        // when
        Store store = Store.create(storeCreate, owner, now);

        // then
        assertThat(store.getName()).isEqualTo("한식당");
        assertThat(store.getStoreType()).isEqualTo(KOREAN_FOOD);
        assertThat(store.getTotalSales()).isZero();
    }

    @Test
    @DisplayName("가게가 오픈했는지 확인한다.")
    void isOpening() throws Exception {
        //given
        LocalDate now = new TestClockHolder().now();
        User owner = buildOwner();
        LocalTime openTime = LocalTime.of(12, 0);
        LocalTime closeTIme = LocalTime.of(21, 0);
        StoreCreate storeCreate = StoreCreate.builder()
                .storeType(KOREAN_FOOD)
                .storeName("한식당")
                .openTime(openTime)
                .closeTime(closeTIme)
                .build();
        Store store = Store.create(storeCreate, owner, now);

        //when
        boolean isOpening = store.isOpening(LocalTime.of(13, 0));

        //then
        assertThat(isOpening).isTrue();
    }

    @Test
    @DisplayName("총 매출과 일일 매출이 같은 날짜면 누적된다.")
    void addTotalSalesWithSameDay() throws Exception {
        // given
        LocalDate now = new TestClockHolder().now();
        User owner = buildOwner();
        LocalTime openTime = LocalTime.of(12, 0);
        LocalTime closeTIme = LocalTime.of(21, 0);

        StoreCreate storeCreate = StoreCreate.builder()
                .storeType(KOREAN_FOOD)
                .storeName("한식당")
                .openTime(openTime)
                .closeTime(closeTIme)
                .build();
        Store store = Store.create(storeCreate, owner, now);

        // when
        Store result = store.addTotalSales(25000, now);

        // then
        assertThat(result.getTotalSales()).isEqualTo(25000);
        assertThat(result.getDailySales()).isEqualTo(25000);
    }

    @Test
    @DisplayName("총 매출은 누적되고 일일 매출은 날짜가 다르면 초기화된다.")
    void addTotalSalesWithDifferentDay() throws Exception {
        // given
        LocalDate yesterday = LocalDate.of(2025, 11, 9);
        LocalDate today = LocalDate.of(2025, 11, 10);

        User owner = buildOwner();
        LocalTime openTime = LocalTime.of(12, 0);
        LocalTime closeTIme = LocalTime.of(21, 0);

        StoreCreate storeCreate = StoreCreate.builder()
                .storeType(KOREAN_FOOD)
                .storeName("한식당")
                .openTime(openTime)
                .closeTime(closeTIme)
                .build();
        Store store = Store.create(storeCreate, owner, yesterday);

        // when & then
        Store firstSale = store.addTotalSales(10000, yesterday);
        assertThat(firstSale.getTotalSales()).isEqualTo(10000);
        assertThat(firstSale.getDailySales()).isEqualTo(10000);

        Store secondSale = firstSale.addTotalSales(10000, today);
        assertThat(secondSale.getTotalSales()).isEqualTo(20000);
        assertThat(secondSale.getDailySales()).isEqualTo(10000);
        assertThat(secondSale.getLastSalesDate()).isEqualTo(today);
    }

    private static User buildOwner() {
        return User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address("대구")
                .role(OWNER)
                .build();
    }

}