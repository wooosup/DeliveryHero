package hello.delivery.product.service.port;

import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.store.domain.Store;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findById(Long id);

    Product save(Product product);

    void deleteById(Long product);

    List<Product> saveAll(List<Product> products);

    List<Product> findAll();

    List<Product> findByProductType(Long id, ProductType type);

    List<Product> findByProductSellingStatusIs(Long id, ProductSellingStatus status);

    Optional<Product> findByIdWithLock(Long id);

    Optional<Product> findByStoreAndNameWithLock(Store store, String name);

    boolean existsByStoreAndName(Store store, String name);

    List<Product> findByStore(Store store);

}
