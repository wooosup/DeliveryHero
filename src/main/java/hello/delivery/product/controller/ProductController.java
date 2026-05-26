package hello.delivery.product.controller;

import hello.delivery.common.annotation.LoginOwnerId;
import hello.delivery.common.api.ApiResponse;
import hello.delivery.product.controller.docs.ProductControllerDocs;
import hello.delivery.product.service.port.in.ProductService;
import hello.delivery.product.controller.response.ProductResponse;
import hello.delivery.product.domain.Product;
import hello.delivery.product.controller.request.ProductCreate;
import hello.delivery.product.controller.request.ProductStatusUpdate;
import hello.delivery.product.domain.ProductType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductControllerDocs {

    private final ProductService productService;

    @Override
    @PostMapping("/new")
    public ApiResponse<ProductResponse> createProduct(@LoginOwnerId Long userId,
                                                      @Valid @RequestBody ProductCreate request) {
        Product product = productService.create(userId, request.toCommand());
        return ApiResponse.ok(ProductResponse.of(product));
    }

    @Override
    @PostMapping("/new/batch")
    public ApiResponse<List<ProductResponse>> createProducts(@LoginOwnerId Long userId,
                                                             @Valid @RequestBody List<ProductCreate> request) {
        List<Product> products = productService.creates(userId, request.stream()
                .map(ProductCreate::toCommand)
                .toList());
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @PatchMapping("/{id}/status")
    public ApiResponse<ProductResponse> changeStatus(@LoginOwnerId Long userId, @PathVariable Long id,
                                                     @Valid @RequestBody ProductStatusUpdate request) {
        Product product = productService.changeSellingStatus(id, userId, request.status());
        return ApiResponse.ok(ProductResponse.of(product));
    }

    @Override
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@LoginOwnerId Long userId, @PathVariable Long id) {
        productService.deleteById(userId, id);
        return ApiResponse.ok(null);
    }

    @Override
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @GetMapping("/selling")
    public ApiResponse<List<ProductResponse>> getSellingProducts(@RequestParam Long storeId) {
        List<Product> products = productService.findBySelling(storeId);
        return ApiResponse.ok(ProductResponse.of(products));
    }

    @Override
    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> getProductsByType(@RequestParam Long storeId,
                                                                @RequestParam ProductType type) {
        List<Product> products = productService.findByType(storeId, type);
        return ApiResponse.ok(ProductResponse.of(products));
    }
}
