package hello.delivery.order;

import static hello.delivery.product.domain.ProductType.FOOD;
import static hello.delivery.store.domain.StoreType.KOREAN_FOOD;
import static org.assertj.core.api.Assertions.assertThat;

import hello.delivery.order.controller.port.OrderService;
import hello.delivery.order.domain.OrderCreate;
import hello.delivery.order.domain.OrderProductRequest;
import hello.delivery.product.controller.port.ProductService;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductCreate;
import hello.delivery.store.controller.port.StoreService;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.user.controller.port.UserService;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderConcurrencyIntegrationTest {

    private static final LocalTime OPEN_TIME = LocalTime.of(0, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 59);

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.6")
            .withDatabaseName("delivery_test")
            .withUsername("test")
            .withPassword("test")
            .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.properties.hibernate.show_sql", () -> "false");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("재고가 1개인 상품에 대한 동시 주문은 1건만 성공하고 재고는 0이 된다.")
    void concurrentOrdersDoNotOversellStock() throws Exception {
        // given
        User owner = createOwner("owner1");
        Store store = storeService.create(owner.getId(), StoreCreate.builder()
                .storeName("동시성-스토어")
                .storeType(KOREAN_FOOD)
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build());
        Product product = productService.create(owner.getId(), ProductCreate.builder()
                .storeName(store.getName())
                .name("치킨")
                .price(15000)
                .type(FOOD)
                .stock(1)
                .build());

        User customerA = createCustomer("custc1");
        User customerB = createCustomer("custc2");
        OrderCreate request = OrderCreate.builder()
                .storeId(store.getId())
                .address("서울시 강남구")
                .orderProducts(List.of(OrderProductRequest.builder()
                        .productId(product.getId())
                        .quantity(1)
                        .build()))
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        List<String> failureMessages = new ArrayList<>();

        try {
            submitOrder(executorService, customerA.getId(), request, ready, start, done, successCount, failCount, failureMessages);
            submitOrder(executorService, customerB.getId(), request, ready, start, done, successCount, failCount, failureMessages);

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();

            start.countDown();

            assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        } finally {
            executorService.shutdownNow();
        }

        Product resultProduct = productService.findByStoreId(store.getId()).stream()
                .filter(savedProduct -> savedProduct.getName().equals("치킨"))
                .findFirst()
                .orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(failureMessages).anyMatch(message ->
                message.contains("재고가 부족합니다.") || message.contains("품절된 상품입니다."));
        assertThat(resultProduct.getStock().getQuantity()).isZero();
    }

    private void submitOrder(ExecutorService executorService,
                             Long customerId,
                             OrderCreate request,
                             CountDownLatch ready,
                             CountDownLatch start,
                             CountDownLatch done,
                             AtomicInteger successCount,
                             AtomicInteger failCount,
                             List<String> failureMessages) {
        executorService.submit(() -> {
            ready.countDown();
            try {
                start.await();
                orderService.order(customerId, request);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
                synchronized (failureMessages) {
                    failureMessages.add(rootCauseMessage(e));
                }
            } finally {
                done.countDown();
            }
        });
    }

    private User createOwner(String username) {
        return userService.signupOwner(UserCreate.builder()
                .name("사장")
                .username(username)
                .password("password1234")
                .address("서울")
                .build());
    }

    private User createCustomer(String username) {
        return userService.signupCustomer(UserCreate.builder()
                .name("고객")
                .username(username)
                .password("password1234")
                .address("서울")
                .build());
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }

}
