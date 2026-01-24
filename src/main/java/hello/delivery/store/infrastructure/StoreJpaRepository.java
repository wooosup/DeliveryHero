package hello.delivery.store.infrastructure;

import hello.delivery.store.domain.StoreType;
import hello.delivery.user.infrastructure.UserEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, Long> {

    List<StoreEntity> findByStoreType(StoreType type);

    Optional<StoreEntity> findByName(String name);

    boolean existsByName(String name);

    @Modifying
    @Query("update StoreEntity s set "
            + "s.dailySales = :dailySales, "
            + "s.totalSales = :totalSales, "
            + "s.lastSalesDate = :lastSalesDate,"
            + "s.openTime = :openTime,"
            + "s.closeTime = :closeTime where s.id = :storeId")
    void updateSales(@Param("storeId") Long storeId,
                     @Param("dailySales") int dailySales,
                     @Param("totalSales") int totalSales,
                     @Param("lastSalesDate") LocalDate lastSalesDate,
                     @Param("openTime") LocalTime openTime,
                     @Param("closeTime") LocalTime closeTime);

    @Query("select s from StoreEntity s where s.owner = :owner")
    List<StoreEntity> findByStoresForOwner(UserEntity owner);
}
