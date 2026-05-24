package hello.delivery.store.service.port;

import static hello.delivery.store.domain.StoreType.JAPANESE_FOOD;
import static hello.delivery.store.domain.StoreType.KOREAN_FOOD;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.assertj.core.api.Assertions.assertThat;

import hello.delivery.common.domain.Address;
import hello.delivery.mock.FakeStoreRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StoreRepositoryTest {

    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        storeRepository = new FakeStoreRepository();
    }

    @Test
    @DisplayName("가게 타입에 맞는 가게들을 조회한다.")
    void findByStoreType() throws Exception {
        // given
        User owner = buildOwner();
        Store store = Store.builder()
                .owner(owner)
                .name("한식당")
                .storeType(KOREAN_FOOD)
                .build();
        Store store2 = Store.builder()
                .owner(owner)
                .name("일식")
                .storeType(JAPANESE_FOOD)
                .build();
        storeRepository.save(store);
        storeRepository.save(store2);

        // when
        List<Store> result = storeRepository.findByStoreType(KOREAN_FOOD);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("한식당");
    }

    private static User buildOwner() {
        return User.builder()
                .id(1L)
                .name("차상훈")
                .username("wss3325")
                .password("hihihi3454")
                .address(Address.of("대구"))
                .role(OWNER)
                .build();
    }

}
