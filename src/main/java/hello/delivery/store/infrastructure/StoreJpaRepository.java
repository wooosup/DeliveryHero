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
            + "s.openTime = :openTime, "
            + "s.closeTime = :closeTime "
            + "where s.id = :storeId")
    void updateBusinessHours(@Param("storeId") Long storeId,
                             @Param("openTime") LocalTime openTime,
                             @Param("closeTime") LocalTime closeTime);

    @Query("select s from StoreEntity s where s.owner = :owner")
    List<StoreEntity> findByStoresForOwner(UserEntity owner);

    @Modifying
    @Query("""
             update StoreEntity s
             set  s.totalSales = s.totalSales + :amount,
                 s.dailySales =
                     case
                         when s.lastSalesDate = :currentDate then s.dailySales + :amount
                         else :amount
                     end,
                 s.lastSalesDate = :currentDate
             where s.id = :storeId
            """)
    int addSales(@Param("storeId") Long storeId,
                 @Param("amount") int amount,
                 @Param("currentDate") LocalDate currentDate);

}
