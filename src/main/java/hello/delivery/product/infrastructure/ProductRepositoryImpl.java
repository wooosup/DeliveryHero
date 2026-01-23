package hello.delivery.product.infrastructure;

import hello.delivery.common.exception.ProductNotFound;
import hello.delivery.product.domain.Product;
import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.product.service.port.ProductRepository;
import hello.delivery.store.domain.Store;
import hello.delivery.store.infrastructure.StoreEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(ProductEntity::toDomain);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(ProductEntity.of(product)).toDomain();
    }

    @Override
    public void deleteById(Long productId) {
        ProductEntity entity = productJpaRepository.findById(productId)
                .orElseThrow(ProductNotFound::new);
        productJpaRepository.delete(entity);
        entity.toDomain();
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        List<ProductEntity> entityList = products.stream()
                .map(ProductEntity::of)
                .toList();

        return productJpaRepository.saveAll(entityList).stream()
                .map(ProductEntity::toDomain)
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(ProductEntity::toDomain)
                .toList();
    }

    @Override
    public List<Product> findByProductType(Long id, ProductType type) {
        return productJpaRepository.findByStoreIdAndProductType(id, type).stream()

                .map(ProductEntity::toDomain)
                .toList();
    }

    @Override
    public List<Product> findByProductSellingStatusIs(Long id, ProductSellingStatus status) {
        return productJpaRepository.findByStoreIdAndProductSellingStatus(id, status).stream()
                .map(ProductEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findByStoreAndNameWithLock(Store store, String name) {
        return productJpaRepository.findByStoreAndNameWithLock(StoreEntity.of(store), name)
                .map(ProductEntity::toDomain);
    }

    @Override
    public boolean existsByStoreAndName(Store store, String name) {
        return productJpaRepository.existsByStoreAndName(StoreEntity.of(store), name);
    }

    @Override
    public List<Product> findByStore(Store store) {
        return productJpaRepository.findByStore(StoreEntity.of(store)).stream()
                .map(ProductEntity::toDomain)
                .toList();
    }
}
