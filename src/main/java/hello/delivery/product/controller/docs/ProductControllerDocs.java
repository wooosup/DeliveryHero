
package hello.delivery.product.controller.docs;

import hello.delivery.common.annotation.LoginOwnerId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.product.controller.response.ProductResponse;
import hello.delivery.product.controller.request.ProductCreate;
import hello.delivery.product.domain.ProductStatusUpdate;
import hello.delivery.product.domain.ProductType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "상품")
public interface ProductControllerDocs {

    @Operation(summary = "상품 등록", description = "사장의 권한으로 새로운 상품을 등록합니다.")
    ApiResponse<ProductResponse> createProduct(
            @Parameter(hidden = true) @LoginOwnerId Long userId,
            @Valid @RequestBody ProductCreate request
    );

    @Operation(summary = "상품 일괄 등록", description = "사장의 권한으로 여러 상품을 한 번에 등록합니다.")
    ApiResponse<List<ProductResponse>> createProducts(
            @Parameter(hidden = true) @LoginOwnerId Long userId,
            @Valid @RequestBody List<ProductCreate> request
    );

    @Operation(summary = "상품 판매 상태 변경", description = "상품의 판매 상태(SELLING, STOP_SELLING)를 변경합니다.")
    ApiResponse<ProductResponse> changeStatus(
            @Parameter(hidden = true) @LoginOwnerId Long userId,
            @Parameter(description = "상품 ID") @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdate request
    );

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    ApiResponse<Void> deleteProduct(
            @Parameter(hidden = true) @LoginOwnerId Long userId,
            @Parameter(description = "상품 ID") @PathVariable Long id
    );

    @Operation(summary = "전체 상품 조회", description = "등록된 모든 상품을 조회합니다.")
    ApiResponse<List<ProductResponse>> getAllProducts();

    @Operation(summary = "판매 중인 상품 조회", description = "판매 상태가 SELLING인 상품만 조회합니다.")
    ApiResponse<List<ProductResponse>> getSellingProducts(
            @Parameter(description = "가게 ID") @RequestParam Long storeId
    );

    @Operation(summary = "타입별 상품 조회", description = "특정 타입(FOOD, BEVERAGE 등)의 상품을 조회합니다.")
    ApiResponse<List<ProductResponse>> getProductsByType(
            @Parameter(description = "가게 ID") @RequestParam Long storeId,
            @Parameter(description = "상품 타입") @RequestParam ProductType type
    );
}
