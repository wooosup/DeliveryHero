package hello.delivery.common;

import hello.delivery.common.domain.Address;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.out.DeliveryRepository;
import hello.delivery.order.domain.Order;
import hello.delivery.order.service.port.in.OrderCommandService;
import hello.delivery.order.service.port.in.OrderCreateCommand;
import hello.delivery.order.service.port.in.OrderProductCommand;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.in.ProductCreateCommand;
import hello.delivery.product.service.port.in.ProductService;
import hello.delivery.rider.domain.Rider;
import hello.delivery.rider.service.port.in.RiderCreateCommand;
import hello.delivery.rider.service.port.in.RiderLoginCommand;
import hello.delivery.rider.service.port.in.RiderService;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.in.StoreCreateCommand;
import hello.delivery.store.service.port.in.StoreService;
import hello.delivery.user.domain.User;
import hello.delivery.user.domain.UserRole;
import hello.delivery.user.service.port.in.SignupCommand;
import hello.delivery.user.service.port.in.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static hello.delivery.user.domain.UserRole.CUSTOMER;
import static hello.delivery.user.domain.UserRole.OWNER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private OrderCommandService orderService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("사장 세션으로 주문 생성 요청을 보내면 403을 반환한다.")
    void orderEndpointRequiresCustomerRole() throws Exception {
        // given
        User owner = createOwner("owner-1");
        Product product = createStoreWithProduct(owner.getId(), "스토어-1", "상품-1");
        Store store = product.getStore();

        // when & then
        mockMvc.perform(post("/api/orders/new")
                        .session(userSession(owner.getId(), OWNER))
                        .contentType(APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "storeId": %d,
                                  "address": "서울시 강남구",
                                  "orderProducts": [
                                    {
                                      "productId": %d,
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """, store.getId(), product.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("인증 없이 주문 생성 요청을 보내면 401을 반환한다.")
    void unauthenticatedOrderRequestReturnsUnauthorized() throws Exception {
        // given
        User owner = createOwner("owner-0");
        Product product = createStoreWithProduct(owner.getId(), "스토어-0", "상품-0");
        Store store = product.getStore();

        // when & then
        mockMvc.perform(post("/api/orders/new")
                        .contentType(APPLICATION_JSON)
                        .content(String.format("""
                                {
                                  "storeId": %d,
                                  "address": "서울시 강남구",
                                  "orderProducts": [
                                    {
                                      "productId": %d,
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """, store.getId(), product.getId())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("고객 세션으로 주문 수락 요청을 보내면 403을 반환한다.")
    void acceptEndpointRequiresOwnerRole() throws Exception {
        // given
        User owner = createOwner("owner-2");
        User customer = createCustomer("customer-1");
        Product product = createStoreWithProduct(owner.getId(), "스토어-2", "상품-2");
        Store store = product.getStore();
        Order order = createOrder(customer.getId(), store, product);

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
        Product product = createStoreWithProduct(owner.getId(), "스토어-3", "상품-3");
        Store store = product.getStore();
        Order order = createOrder(customerA.getId(), store, product);

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

    private Product createStoreWithProduct(Long ownerId, String storeName, String productName) {
        Store store = storeService.create(ownerId, StoreCreateCommand.of(
                storeName,
                StoreType.KOREAN_FOOD,
                OPEN_TIME,
                CLOSE_TIME
        ));

        return productService.create(ownerId, ProductCreateCommand.of(
                store.getName(),
                productName,
                10000,
                ProductType.FOOD,
                10
        ));
    }

    private Order createOrder(Long customerId, Store store, Product product) {
        return orderService.order(customerId, OrderCreateCommand.of(
                store.getId(),
                List.of(OrderProductCommand.of(product.getId(), 1)),
                "서울시 강남구"
        ));
    }

    private Delivery createAssignedDelivery(String ownerUsername,
                                            String customerUsername,
                                            String storeName,
                                            String productName,
                                            String riderPhone) {
        User owner = createOwner(ownerUsername);
        User customer = createCustomer(customerUsername);
        Product product = createStoreWithProduct(owner.getId(), storeName, productName);
        Store store = product.getStore();
        Order order = createOrder(customer.getId(), store, product);
        Order acceptedOrder = orderService.accept(owner.getId(), order.getId());

        Rider rider = createAvailableRider(riderPhone);
        Delivery delivery = deliveryRepository.save(Delivery.create(acceptedOrder));
        return deliveryRepository.save(delivery.assign(rider.getId()));
    }

    private User createOwner(String username) {
        return userService.signupOwner(SignupCommand.of(
                "사장",
                username,
                "password1234",
                Address.of("서울")
        ));
    }

    private User createCustomer(String username) {
        return userService.signupCustomer(SignupCommand.of(
                "고객",
                username,
                "password1234",
                Address.of("서울")
        ));
    }

    private Rider createAvailableRider(String phone) {
        riderService.signup(RiderCreateCommand.of("라이더", phone));

        return riderService.login(RiderLoginCommand.of(phone));
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
