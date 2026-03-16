package hello.delivery.common;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.DeliveryRepository;
import hello.delivery.order.controller.port.OrderService;
import hello.delivery.order.domain.Order;
import hello.delivery.order.domain.OrderCreate;
import hello.delivery.order.domain.OrderProductRequest;
import hello.delivery.product.controller.port.ProductService;
import hello.delivery.product.domain.ProductCreate;
import hello.delivery.product.domain.ProductType;
import hello.delivery.rider.controller.port.RiderService;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.domain.RiderCreate;
import hello.delivery.rider.domain.RiderLogin;
import hello.delivery.store.controller.port.StoreService;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.store.domain.StoreType;
import hello.delivery.user.controller.port.UserService;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserCreate;
import hello.delivery.user.domain.UserRole;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthorizationIntegrationTest {

    private static final LocalTime OPEN_TIME = LocalTime.of(0, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(23, 59);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("사장 세션으로 주문 생성 요청을 보내면 403을 반환한다.")
    void orderEndpointRequiresCustomerRole() throws Exception {
        // given
        User owner = createOwner("owner-1");
        createStoreWithProduct(owner.getId(), "스토어-1", "상품-1");

        // when & then
        mockMvc.perform(post("/api/orders/new")
                        .session(userSession(owner.getId(), OWNER))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "storeName": "스토어-1",
                                  "address": "서울시 강남구",
                                  "orderProducts": [
                                    {
                                      "productName": "상품-1",
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("인증 없이 주문 생성 요청을 보내면 401을 반환한다.")
    void unauthenticatedOrderRequestReturnsUnauthorized() throws Exception {
        // given
        User owner = createOwner("owner-0");
        createStoreWithProduct(owner.getId(), "스토어-0", "상품-0");

        // when & then
        mockMvc.perform(post("/api/orders/new")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "storeName": "스토어-0",
                                  "address": "서울시 강남구",
                                  "orderProducts": [
                                    {
                                      "productName": "상품-0",
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("고객 세션으로 주문 수락 요청을 보내면 403을 반환한다.")
    void acceptEndpointRequiresOwnerRole() throws Exception {
        // given
        User owner = createOwner("owner-2");
        User customer = createCustomer("customer-1");
        createStoreWithProduct(owner.getId(), "스토어-2", "상품-2");
        Order order = createOrder(customer.getId(), "스토어-2", "상품-2");

        // when & then
        mockMvc.perform(post("/api/orders/accept/{orderId}", order.getId())
                        .session(userSession(customer.getId(), CUSTOMER)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("다른 고객이 남의 주문을 취소하려고 하면 403을 반환한다.")
    void anotherCustomerCannotCancelOthersOrder() throws Exception {
        // given
        User owner = createOwner("owner-3");
        User customerA = createCustomer("customer-2");
        User customerB = createCustomer("customer-3");
        createStoreWithProduct(owner.getId(), "스토어-3", "상품-3");
        Order order = createOrder(customerA.getId(), "스토어-3", "상품-3");

        // when & then
        mockMvc.perform(post("/api/orders/cancel/{orderId}", order.getId())
                        .session(userSession(customerB.getId(), CUSTOMER)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("인증 없이 배달 조회를 호출하면 401을 반환한다.")
    void unauthenticatedDeliveryQueryReturnsUnauthorized() throws Exception {
        // given
        Delivery delivery = createAssignedDelivery("owner-4", "customer-4", "스토어-4", "상품-4", "010-1111-1111");

        // when & then
        mockMvc.perform(get("/api/deliveries/{deliveryId}", delivery.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다른 라이더가 남의 배달을 조회하려고 하면 403을 반환한다.")
    void anotherRiderCannotReadOthersDelivery() throws Exception {
        // given
        Delivery delivery = createAssignedDelivery("owner-5", "customer-5", "스토어-5", "상품-5", "010-2222-2222");
        Rider otherRider = createAvailableRider("010-3333-3333");

        // when & then
        mockMvc.perform(get("/api/deliveries/{deliveryId}", delivery.getId())
                        .session(riderSession(otherRider.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("다른 라이더가 남의 배달을 완료하려고 하면 403을 반환한다.")
    void anotherRiderCannotCompleteOthersDelivery() throws Exception {
        // given
        Delivery delivery = createAssignedDelivery("owner-6", "customer-6", "스토어-6", "상품-6", "010-4444-4444");
        Rider otherRider = createAvailableRider("010-5555-5555");

        // when & then
        mockMvc.perform(patch("/api/deliveries/{deliveryId}/complete", delivery.getId())
                        .session(riderSession(otherRider.getId())))
                .andExpect(status().isForbidden());
    }

    private void createStoreWithProduct(Long ownerId, String storeName, String productName) {
        storeService.create(ownerId, StoreCreate.builder()
                .storeName(storeName)
                .storeType(StoreType.KOREAN_FOOD)
                .openTime(OPEN_TIME)
                .closeTime(CLOSE_TIME)
                .build());

        productService.create(ownerId, ProductCreate.builder()
                .storeName(storeName)
                .name(productName)
                .price(10000)
                .type(ProductType.FOOD)
                .stock(10)
                .build());
    }

    private Order createOrder(Long customerId, String storeName, String productName) {
        return orderService.order(customerId, OrderCreate.builder()
                .storeName(storeName)
                .address("서울시 강남구")
                .orderProducts(List.of(OrderProductRequest.builder()
                        .productName(productName)
                        .quantity(1)
                        .build()))
                .build());
    }

    private Delivery createAssignedDelivery(String ownerUsername,
                                            String customerUsername,
                                            String storeName,
                                            String productName,
                                            String riderPhone) {
        User owner = createOwner(ownerUsername);
        User customer = createCustomer(customerUsername);
        createStoreWithProduct(owner.getId(), storeName, productName);
        Order order = createOrder(customer.getId(), storeName, productName);
        orderService.accept(owner.getId(), order.getId());

        Rider rider = createAvailableRider(riderPhone);
        Delivery delivery = deliveryRepository.findByOrderId(order.getId()).orElseThrow();
        return deliveryRepository.save(delivery.assign(rider.getId()));
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

    private Rider createAvailableRider(String phone) {
        riderService.signup(RiderCreate.builder()
                .name("라이더")
                .phone(phone)
                .build());

        return riderService.login(RiderLogin.builder()
                .phone(phone)
                .build());
    }

    private MockHttpSession userSession(Long userId, UserRole role) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        session.setAttribute("userRole", role);
        return session;
    }

    private MockHttpSession riderSession(Long riderId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("riderId", riderId);
        return session;
    }
}
