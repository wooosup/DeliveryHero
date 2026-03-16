package hello.delivery.delivery.controller.docs;

import hello.delivery.common.annotation.LoginRiderId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.delivery.controller.response.DeliveryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "배달")
public interface DeliveryControllerDocs {

    @Operation(summary = "배차 완료", description = "배달을 시작 상태(ASSIGNED)로 변경합니다. (라이더용)")
    ApiResponse<DeliveryResponse> assign(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Parameter(description = "배달 ID") @PathVariable Long deliveryId);

    @Operation(summary = "배달 시작", description = "배달을 시작 상태(PICKED_UP)로 변경합니다. (라이더용)")
    ApiResponse<DeliveryResponse> start(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Parameter(description = "배달 ID") @PathVariable Long deliveryId);

    @Operation(summary = "배달 완료", description = "배달을 완료 상태(DELIVERED)로 변경합니다. (라이더용)")
    ApiResponse<DeliveryResponse> complete(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Parameter(description = "배달 ID") @PathVariable Long deliveryId);

    @Operation(summary = "배달 조회", description = "배달 ID로 배달 정보를 조회합니다. (라이더용)")
    ApiResponse<DeliveryResponse> getDeliveryById(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Parameter(description = "배달 ID") @PathVariable Long deliveryId);

    @Operation(summary = "배달 주문 조회", description = "배달에 해당하는 주문 정보를 조회합니다. (라이더용)")
    ApiResponse<DeliveryResponse> getOrderForDelivery(
            @Parameter(hidden = true) @LoginRiderId Long riderId,
            @Parameter(description = "배달 ID") @PathVariable Long deliveryId);

}
