package hello.delivery.product.infrastructure;

import hello.delivery.product.domain.ProductSellingStatus;
import hello.delivery.product.domain.ProductType;
import hello.delivery.store.infrastructure.StoreEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByStoreIdAndProductType(Long storeId, ProductType productType);

    List<ProductEntity> findByStoreIdAndProductSellingStatus(Long storeId, ProductSellingStatus productSellingStatus);

    // Hold a row lock through order creation so concurrent decrements cannot oversell the same product.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.store = :store and p.name = :name")
    Optional<ProductEntity> findByStoreAndNameWithLock(@Param("store") StoreEntity store, String name);

    List<ProductEntity> findByStore(StoreEntity store);

    boolean existsByStoreAndName(StoreEntity storeEntity, String name);

}
