package hello.delivery.product.service.port.in;

import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import java.util.List;

public interface ProductService {

    Product create(Long userId, ProductCreateCommand command);

    List<Product> creates(Long userId, List<ProductCreateCommand> commands);

    Product changeSellingStatus(Long id, Long userId, ProductSellingStatus status);

    void deleteById(Long ownerId, Long productId);

    List<Product> findAll();

    List<Product> findByType(Long storeId, ProductType type);

    List<Product> findBySelling(Long storeId);

    List<Product> findByStoreId(Long storeId);

}
