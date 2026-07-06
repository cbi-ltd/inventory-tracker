package org.inventory_tracker.repository;

import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StationInventoryRepository extends JpaRepository<StationInventory,Long> {

    @Query("""
        SELECT s
        FROM StationInventory s
        WHERE s.outletId = :outletId
        AND s.productType = :productType
        AND s.businessDate = CURRENT_DATE
    """)
    Optional<StationInventory> findTodayInventory(@Param("outletId") String outletId, @Param("productType") ProductType productType);
}
