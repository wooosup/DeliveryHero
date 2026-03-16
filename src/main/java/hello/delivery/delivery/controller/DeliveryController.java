package hello.delivery.delivery.controller;

import hello.delivery.common.annotation.LoginRiderId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.delivery.controller.docs.DeliveryControllerDocs;
import hello.delivery.delivery.controller.port.DeliveryService;
import hello.delivery.delivery.controller.response.DeliveryResponse;
import hello.delivery.delivery.domain.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerDocs {

    private final DeliveryService deliveryService;

    @Override
    @PatchMapping("/{deliveryId}/assign")
    public ApiResponse<DeliveryResponse> assign(@LoginRiderId Long riderId, @PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.assign(deliveryId, riderId);
        return ApiResponse.ok(DeliveryResponse.of(delivery));
    }

    @Override
    @PatchMapping("/{deliveryId}/start")
    public ApiResponse<DeliveryResponse> start(@LoginRiderId Long riderId, @PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.start(deliveryId, riderId);
        return ApiResponse.ok(DeliveryResponse.of(delivery));
    }

    @Override
    @PatchMapping("/{deliveryId}/complete")
    public ApiResponse<DeliveryResponse> complete(@LoginRiderId Long riderId, @PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.complete(deliveryId, riderId);
        return ApiResponse.ok(DeliveryResponse.of(delivery));
    }

    @Override
    @GetMapping("/{deliveryId}")
    public ApiResponse<DeliveryResponse> getDeliveryById(@LoginRiderId Long riderId, @PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.findById(riderId, deliveryId);
        return ApiResponse.ok(DeliveryResponse.of(delivery));
    }

    @Override
    @GetMapping("/order/{deliveryId}")
    public ApiResponse<DeliveryResponse> getOrderForDelivery(@LoginRiderId Long riderId, @PathVariable Long deliveryId) {
        Delivery delivery = deliveryService.findByOrderId(riderId, deliveryId);
        return ApiResponse.ok(DeliveryResponse.of(delivery));
    }

}
