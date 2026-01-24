package hello.delivery.store.controller.docs;

import hello.delivery.common.annotation.LoginUser;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.product.controller.response.ProductResponse;
import hello.delivery.product.domain.ProductType;
import hello.delivery.store.controller.response.StoreCustomerResponse;
import hello.delivery.store.controller.response.StoreOwnerResponse;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.store.domain.StoreType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "가게")
public interface StoreControllerDocs {

    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다.")
    ApiResponse<StoreOwnerResponse> createStore(
            @Parameter(hidden = true) @LoginUser Long userId,
            @Valid @RequestBody StoreCreate request
    );

    @Operation(summary = "가게 타입별 조회", description = "가게 타입에 해당하는 가게들을 조회합니다.")
    ApiResponse<List<StoreCustomerResponse>> getStoresByType(
            @Parameter(description = "가게 타입") @PathVariable StoreType type);

    @Operation(summary = "자신의 가게 조회", description = "자신이 등록한 가게를 조회합니다.")
    ApiResponse<List<StoreOwnerResponse>> getMyStores(@Parameter(hidden = true) @LoginUser Long userId);

    @Operation(summary = "전체 가게 조회", description = "등록된 모든 가게들을 조회합니다.")
    ApiResponse<List<StoreCustomerResponse>> findAll();

    @Operation(summary = "가게 이름별 조회", description = "가게 이름에 해당하는 가게를 조회합니다.")
    ApiResponse<StoreCustomerResponse> searchByName(@RequestParam String name);

    @Operation(summary = "가게별 상품 조회", description = "특정 가게에 속한 모든 상품을 조회합니다.")
    ApiResponse<List<ProductResponse>> getProductsByStore(
            @Parameter(description = "가게 ID") @PathVariable Long storeId);

    @Operation(summary = "가게별 판매 중인 상품 조회", description = "특정 가게에 속한 판매 중인 상품들을 조회합니다.")
    ApiResponse<List<ProductResponse>> getSellingProducts(
            @Parameter(description = "가게 ID") @PathVariable Long storeId);

    @Operation(summary = "가게별 상품 타입 조회", description = "특정 가게에 속한 특정 타입의 상품들을 조회합니다.")
    ApiResponse<List<ProductResponse>> getProductsByType(
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "가게 타입") @PathVariable ProductType type
    );

    @Operation(summary = "가게 오픈 시간 변경", description = "가게의 영업 시작 시간을 변경합니다.")
    ApiResponse<StoreOwnerResponse> changeOpenTime(
            @Parameter(hidden = true) @LoginUser Long userId,
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "변경할 시간 (예: 09:00:00)", example = "09:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newOpenTime
    );

    @Operation(summary = "가게 마감 시간 변경", description = "가게의 영업 종료 시간을 변경합니다.")
    ApiResponse<StoreOwnerResponse> changeCloseTime(
            @Parameter(hidden = true) @LoginUser Long userId,
            @Parameter(description = "가게 ID") @PathVariable Long storeId,
            @Parameter(description = "변경할 시간 (예: 22:00:00)", example = "22:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newCloseTime
    );

}
