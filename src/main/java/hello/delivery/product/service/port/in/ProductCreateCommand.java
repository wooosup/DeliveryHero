package hello.delivery.product.service.port.in;

import hello.delivery.product.domain.ProductType;

public record ProductCreateCommand(
        String storeName,
        String name,
        int price,
        ProductType type,
        Integer stock
) {

    public static ProductCreateCommand of(String storeName, String name, int price, ProductType type, Integer stock) {
        return new ProductCreateCommand(storeName, name, price, type, stock);
    }

}
