package hello.delivery.product.controller.response;

import hello.delivery.product.domain.Product;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final String type;
    private final String sellingStatus;
    private final Long storeId;
    private final String storeName;

    @Builder
    private ProductResponse(Long id, String name, int price, String type, String sellingStatus, Long storeId,
                           String storeName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice().getAmount())
                .type(product.getProductType().name())
                .sellingStatus(product.getProductSellingStatus().name())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .build();
    }

    public static List<ProductResponse> of(List<Product> products) {
        return products.stream()
                .map(ProductResponse::of)
                .toList();
    }

}
