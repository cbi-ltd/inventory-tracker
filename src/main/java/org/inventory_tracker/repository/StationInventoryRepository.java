package org.inventory_tracker.repository;

import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
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


    Optional<StationInventory> findByStationIdAndProductId(
            Long stationId,
            Long productId
    );

    boolean existsByStationIdAndProductId(
            Long stationId,
            Long productId
    );

    List<StationInventory> findByStationIdOrderByProduct_NameAsc(
            Long stationId
    );

    List<StationInventory> findByProductIdOrderByStation_NameAsc(
            Long productId
    );

    List<StationInventory> findByActiveTrueOrderByStation_NameAsc();

    List<StationInventory> findAllByOrderByStation_NameAscProduct_NameAsc();

    long countByStationId(Long stationId);

    @Query("""
    SELECT COUNT(si)
    FROM StationInventory si
    WHERE si.station.id = :stationId
      AND si.currentQuantity <= si.reorderLevel""")
    long countLowStockProductsByStation(Long stationId);

    @Query("""
    SELECT COUNT(si)
    FROM StationInventory si
    WHERE si.currentQuantity <= si.reorderLevel""")
    long countByCurrentQuantityLessThanEqualReorderLevel();

    @Query("""
    SELECT COUNT(DISTINCT si.station.id)
    FROM StationInventory si
    WHERE si.currentQuantity <= si.reorderLevel""")
    long countDistinctStationsWithLowStock();

    List<StationInventory> findByStationId(Long stationId);

    List<StationInventory> findByProductId(Long productId);

    long countDistinctStationsByProductId(Long productId);
}
