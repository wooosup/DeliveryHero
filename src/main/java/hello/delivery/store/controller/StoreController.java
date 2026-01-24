package hello.delivery.store.controller;

import hello.delivery.common.annotation.LoginUser;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.common.service.port.ClockHolder;
import hello.delivery.product.controller.port.ProductService;
import hello.delivery.product.controller.response.ProductResponse;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductType;
import hello.delivery.store.controller.docs.StoreControllerDocs;
import hello.delivery.store.controller.port.StoreService;
import hello.delivery.store.controller.response.StoreCustomerResponse;
import hello.delivery.store.controller.response.StoreOwnerResponse;
import hello.delivery.store.domain.Store;
import hello.delivery.store.domain.StoreCreate;
import hello.delivery.store.domain.StoreType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;
    private final ProductService productService;
    private final ClockHolder clockHolder;

    @Override
    @PostMapping("/new")
    public ApiResponse<StoreOwnerResponse> createStore(@LoginUser Long userId,
                                                       @Valid @RequestBody StoreCreate request) {
        Store store = storeService.create(userId, request);
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
    public ApiResponse<List<StoreOwnerResponse>> getMyStores(@LoginUser Long userId) {
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

}
