package hello.delivery.store.controller;

import hello.delivery.common.annotation.LoginOwnerId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.common.service.port.out.ClockHolder;
import hello.delivery.product.controller.response.ProductResponse;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.in.ProductService;
import hello.delivery.store.controller.docs.StoreControllerDocs;
import hello.delivery.store.controller.request.StoreCreate;
import hello.delivery.store.controller.response.StoreCustomerResponse;
import hello.delivery.store.controller.response.StoreOwnerResponse;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreType;
import hello.delivery.store.service.port.in.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;
    private final ProductService productService;
    private final ClockHolder clockHolder;

    @Override
    @PostMapping("/new")
    public ApiResponse<StoreOwnerResponse> createStore(@LoginOwnerId Long userId,
                                                       @Valid @RequestBody StoreCreate request) {
        Store store = storeService.create(userId, request.toCommand());
        return ApiResponse.ok(StoreOwnerResponse.of(store));
    }

    @Override
    @GetMapping("/type/{type}")
    public ApiResponse<List<StoreCustomerResponse>> getStoresByType(@PathVariable StoreType type) {
        List<Store> stores = storeService.findByStoreType(type);
        return ApiResponse.ok(StoreCustomerResponse.of(stores, clockHolder));
    }

    @Override
    @GetMapping("/owner")
    public ApiResponse<List<StoreOwnerResponse>> getMyStores(@LoginOwnerId Long userId) {
        List<Store> stores = storeService.findByOwnerId(userId);
        return ApiResponse.ok(StoreOwnerResponse.of(stores));
    }

    @Override
    @GetMapping("/all")
    public ApiResponse<List<StoreCustomerResponse>> findAll() {
        List<Store> stores = storeService.findAll();
        return ApiResponse.ok(StoreCustomerResponse.of(stores, clockHolder));
    }

    @Override
    @GetMapping("/search")
    public ApiResponse<StoreCustomerResponse> searchByName(@RequestParam String name) {
        Store store = storeService.findByName(name);
        return ApiResponse.ok(StoreCustomerResponse.of(store, clockHolder));
    }

    @Override
    @GetMapping("/{storeId}")
    public ApiResponse<List<ProductResponse>> getProductsByStore(@PathVariable Long storeId) {
        List<Product> products = productService.findByStoreId(storeId);
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @GetMapping("/{storeId}/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts(@PathVariable Long storeId) {
        List<Product> products = productService.findBySelling(storeId);
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @GetMapping("/{storeId}/type/{type}")
    public ApiResponse<List<ProductResponse>> getProductsByType(@PathVariable Long storeId,
                                                                @PathVariable ProductType type) {
        List<Product> products = productService.findByType(storeId, type);
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @PatchMapping("/{storeId}/open-time")
    public ApiResponse<StoreOwnerResponse> changeOpenTime(@LoginOwnerId Long userId,
                                                          @PathVariable Long storeId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newOpenTime) {
        Store store = storeService.changeOpenTime(userId, storeId, newOpenTime);
        return ApiResponse.ok(StoreOwnerResponse.of(store));
    }

    @Override
    @PatchMapping("/{storeId}/close-time")
    public ApiResponse<StoreOwnerResponse> changeCloseTime(@LoginOwnerId Long userId,
                                                           @PathVariable Long storeId,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newCloseTime) {
        Store store = storeService.changeCloseTime(userId, storeId, newCloseTime);
        return ApiResponse.ok(StoreOwnerResponse.of(store));
    }

}
