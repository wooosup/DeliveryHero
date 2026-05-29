package hello.delivery;

import static hello.delivery.delivery.domain.DeliveryStatus.ASSIGNED;
import static hello.delivery.delivery.domain.DeliveryStatus.DELIVERED;
import static hello.delivery.delivery.domain.DeliveryStatus.PICKED_UP;
import static hello.delivery.order.domain.OrderStatus.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.delivery.delivery.domain.Delivery;
import hello.delivery.delivery.service.port.out.DeliveryRepository;
import hello.delivery.order.service.port.out.OrderRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
class DeliveryWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("회원가입부터 배달 완료까지 전체 주문 흐름이 동작한다.")
    void completeDeliveryWorkflow() throws Exception {
        // given
        String ownerUsername = "owner-flow";
        String customerUsername = "customer-flow";
        String riderPhone = "010-9000-0000";
        String storeName = "Flow Store";

        signupOwner(ownerUsername);
        MockHttpSession ownerSession = loginUser(ownerUsername);

        Long storeId = createStore(ownerSession, storeName);
        Long productId = createProduct(ownerSession, storeName);

        signupCustomer(customerUsername);
        MockHttpSession customerSession = loginUser(customerUsername);

        Long orderId = createOrder(customerSession, storeId, productId);

        signupRider(riderPhone);
        MockHttpSession riderSession = loginRider(riderPhone);

        // when & then
        acceptOrder(ownerSession, orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId).orElseThrow();
        Long deliveryId = delivery.getId();

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/assign", deliveryId)
                        .session(riderSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deliveryId").value(deliveryId))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value(ASSIGNED.name()));

        mockMvc.perform(get("/api/deliveries/order/{orderId}", orderId)
                        .session(riderSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deliveryId").value(deliveryId))
                .andExpect(jsonPath("$.data.orderId").value(orderId));

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/start", deliveryId)
                        .session(riderSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(PICKED_UP.name()));

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/complete", deliveryId)
                        .session(riderSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(DELIVERED.name()));

        assertThat(orderRepository.findById(orderId).orElseThrow().getOrderStatus()).isEqualTo(COMPLETED);
    }

    private void signupOwner(String username) throws Exception {
        mockMvc.perform(post("/api/users/owners/signup")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "사장",
                                  "username": "%s",
                                  "password": "password1234",
                                  "address": "서울"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk());
    }

    private void signupCustomer(String username) throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "고객",
                                  "username": "%s",
                                  "password": "password1234",
                                  "address": "서울"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk());
    }

    private MockHttpSession loginUser(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "password1234"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private Long createStore(MockHttpSession ownerSession, String storeName) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/stores/new")
                        .session(ownerSession)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "storeName": "%s",
                                  "storeType": "KOREAN_FOOD",
                                  "openTime": "00:00:00",
                                  "closeTime": "23:59:00"
                                }
                                """.formatted(storeName)))
                .andExpect(status().isOk())
                .andReturn();

        return readLong(result, "/data/id");
    }

    private Long createProduct(MockHttpSession ownerSession, String storeName) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/products/new")
                        .session(ownerSession)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "storeName": "%s",
                                  "name": "흐름상품",
                                  "price": 12000,
                                  "type": "FOOD",
                                  "stock": 5
                                }
                                """.formatted(storeName)))
                .andExpect(status().isOk())
                .andReturn();

        return readLong(result, "/data/id");
    }

    private Long createOrder(MockHttpSession customerSession, Long storeId, Long productId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders/new")
                        .session(customerSession)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "storeId": %d,
                                  "address": "서울 강남구",
                                  "orderProducts": [
                                    {
                                      "productId": %d,
                                      "quantity": 1
                                    }
                                  ]
                                }
                                """.formatted(storeId, productId)))
                .andExpect(status().isOk())
                .andReturn();

        return readLong(result, "/data/id");
    }

    private void acceptOrder(MockHttpSession ownerSession, Long orderId) throws Exception {
        mockMvc.perform(post("/api/orders/accept/{orderId}", orderId)
                        .session(ownerSession))
                .andExpect(status().isOk());
    }

    private void signupRider(String phone) throws Exception {
        mockMvc.perform(post("/api/riders/new")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "라이더",
                                  "phone": "%s"
                                }
                                """.formatted(phone)))
                .andExpect(status().isOk());
    }

    private MockHttpSession loginRider(String phone) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/riders/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "phone": "%s"
                                }
                                """.formatted(phone)))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private Long readLong(MvcResult result, String path) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
        return json.at(path).asLong();
    }
}
