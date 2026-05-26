package hello.delivery.store.service;

import hello.delivery.common.domain.Address;
import hello.delivery.common.exception.UserException;
import hello.delivery.mock.FakeFinder;
import hello.delivery.mock.FakeStoreRepository;
import hello.delivery.mock.TestClockHolder;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.in.StoreCreateCommand;
import hello.delivery.store.service.port.in.StoreService;
import hello.delivery.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static hello.delivery.store.domain.StoreType.JAPANESE_FOOD;
import static hello.delivery.store.domain.StoreType.KOREAN_FOOD;
import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreServiceImplTest {

    public static final LocalTime OPEN_TIME = LocalTime.of(12, 0);
    public static final LocalTime CLOSE_TIME = LocalTime.of(21, 0);

    private StoreService storeService;
    private FakeFinder fakeFinder;
    private FakeStoreRepository fakeStoreRepository;
    private TestClockHolder testClockHolder;

    private User owner;

    @BeforeEach
    void setUp() {
        fakeStoreRepository = new FakeStoreRepository();
        fakeFinder = new FakeFinder();
        testClockHolder = new TestClockHolder();
        storeService = new StoreServiceImpl(fakeStoreRepository, fakeFinder, fakeFinder, testClockHolder);

        owner = buildOwner();
    }

    @Test
    @DisplayName("가게를 생성할 수 있다.")
    void create() {
        // given
        StoreCreateCommand storeCreate = createStoreCreate("한식당", KOREAN_FOOD);

        // when
        Store store = storeService.create(owner.getId(), storeCreate);

        // then
        assertThat(store.getName()).isEqualTo("한식당");
        assertThat(store.getStoreType()).isEqualTo(KOREAN_FOOD);
        assertThat(store.getTotalSales()).isZero();
    }

    @Test
    @DisplayName("오픈시간을 변경 할 수 있다.")
    void changeOpenTime() throws Exception {
        //given
        StoreCreateCommand storeCreate = createStoreCreate("한식당", KOREAN_FOOD);
        Store store = storeService.create(owner.getId(), storeCreate);

        fakeFinder.addStore(store);
        LocalTime newOpenTime = LocalTime.of(8, 0);

        //when
        Store openedStore = storeService.changeOpenTime(store.getOwner().getId(), store.getId(), newOpenTime);

        //then
        assertThat(openedStore.getOpenTime()).isNotEqualTo(OPEN_TIME);
        assertThat(openedStore.isOpening(LocalTime.of(8, 30))).isTrue();
    }

    @Test
    @DisplayName("마감시간을 변경 할 수 있다.")
    void changeCloseTIme() throws Exception {
        //given
        StoreCreateCommand storeCreate = createStoreCreate("한식당", KOREAN_FOOD);
        Store store = storeService.create(owner.getId(), storeCreate);

        fakeFinder.addStore(store);
        LocalTime newCloseTime = LocalTime.of(23, 0);

        //when
        Store openedStore = storeService.changeCloseTime(store.getOwner().getId(), store.getId(), newCloseTime);

        //then
        assertThat(openedStore.getCloseTime()).isNotEqualTo(CLOSE_TIME);
        assertThat(openedStore.isOpening(LocalTime.of(22, 50))).isTrue();
    }

    @Test
    @DisplayName("고객이 가게를 생성하면 예외를 던진다.")
    void validateCreate() {
        // given
        User customer = buildCustomer();
        StoreCreateCommand storeCreate = createStoreCreate("한식당", KOREAN_FOOD);

        // expect
        assertThatThrownBy(() -> storeService.create(customer.getId(), storeCreate))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("권한이 없습니다.");
    }

    @Test
    @DisplayName("가게 타입별로 가게를 조회할 수 있다.")
    void findByStoreType() {
        // given
        StoreCreateCommand koreanStore = createStoreCreate("한식당", KOREAN_FOOD);
        StoreCreateCommand japaneseStore = createStoreCreate("일식당", JAPANESE_FOOD);
        storeService.create(owner.getId(), koreanStore);
        storeService.create(owner.getId(), japaneseStore);

        // when
        List<Store> stores = storeService.findByStoreType(KOREAN_FOOD);

        // then
        assertThat(stores).hasSize(1);
        assertThat(stores.get(0).getStoreType()).isEqualTo(KOREAN_FOOD);
        assertThat(stores.get(0).getName()).isEqualTo("한식당");
    }

    @Test
    @DisplayName("동시에 매출을 추가해도 총 매출과 일일 매출이 누락 없이 누적된다.")
    void addTotalSalesConcurrently() throws InterruptedException {
        // given
        Store store = storeService.create(owner.getId(), createStoreCreate("한식당", KOREAN_FOOD));
        int workerCount = 20;
        int amount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(workerCount);
        CountDownLatch ready = new CountDownLatch(workerCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(workerCount);

        // when
        for (int i = 0; i < workerCount; i++) {
            executorService.submit(() -> {
                try {
                    ready.countDown();
                    start.await();
                    storeService.addTotalSales(store.getId(), amount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(ready.await(1, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        assertThat(done.await(3, TimeUnit.SECONDS)).isTrue();
        executorService.shutdown();
        assertThat(executorService.awaitTermination(1, TimeUnit.SECONDS)).isTrue();

        // then
        Store result = fakeStoreRepository.findById(store.getId()).orElseThrow();
        assertThat(result.getTotalSales()).isEqualTo(workerCount * amount);
        assertThat(result.getDailySales()).isEqualTo(workerCount * amount);
        assertThat(result.getLastSalesDate()).isEqualTo(testClockHolder.now());
    }

    private StoreCreateCommand createStoreCreate(String storeName, StoreType storeType) {
        return StoreCreateCommand.of(storeName, storeType, OPEN_TIME, CLOSE_TIME);
    }

    private User buildOwner() {
        User owner = User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address(Address.of("대구"))
                .role(OWNER)
                .build();
        fakeFinder.addUser(owner);
        return owner;
    }

    private User buildCustomer() {
        User customer = User.builder()
                .id(2L)
                .name("고객")
                .username("customer1")
                .password("password")
                .address(Address.of("서울"))
                .role(CUSTOMER)
                .build();
        fakeFinder.addUser(customer);
        return customer;
    }

}
