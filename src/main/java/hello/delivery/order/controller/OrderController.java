package hello.delivery.order.controller;

import hello.delivery.common.annotation.LoginCustomerId;
import hello.delivery.common.annotation.LoginOwnerId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.order.controller.docs.OrderControllerDocs;
import hello.delivery.order.controller.request.OrderCreate;
import hello.delivery.order.controller.response.OrderResponse;
import hello.delivery.order.domain.Order;
import hello.delivery.order.query.OrderQueryResult;
import hello.delivery.order.service.port.in.OrderCommandService;
import hello.delivery.order.service.port.in.OrderQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @Override
    @PostMapping("/new")
    public ApiResponse<OrderResponse> order(@LoginCustomerId Long customerId,
                                            @Valid @RequestBody OrderCreate request) {
        Order order = orderCommandService.order(customerId, request.toCommand());
        return ApiResponse.ok(OrderResponse.of(order));
    }

    @Override
    @PostMapping("/accept/{orderId}")
    public ApiResponse<OrderResponse> accept(@LoginOwnerId Long ownerId, @PathVariable Long orderId) {
        Order order = orderCommandService.accept(ownerId, orderId);
        return ApiResponse.ok(OrderResponse.of(order));
    }

    @Override
    @PostMapping("/reject/{orderId}")
    public ApiResponse<OrderResponse> reject(@LoginOwnerId Long ownerId, @PathVariable Long orderId) {
        Order order = orderCommandService.reject(ownerId, orderId);
        return ApiResponse.ok(OrderResponse.of(order));
    }

    @Override
    @PostMapping("/cancel/{orderId}")
    public ApiResponse<OrderResponse> cancel(@LoginCustomerId Long customerId, @PathVariable Long orderId) {
        Order order = orderCommandService.cancel(customerId, orderId);
        return ApiResponse.ok(OrderResponse.of(order));
    }

    @Override
    @GetMapping("/my-orders")
    public ApiResponse<List<OrderResponse>> getMyOrders(@LoginCustomerId Long customerId) {
        List<OrderQueryResult> orders = orderQueryService.findOrdersByUserId(customerId);
        return ApiResponse.ok(OrderResponse.fromQueryResults(orders));
    }

}
