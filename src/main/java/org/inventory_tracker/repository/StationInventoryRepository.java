package org.inventory_tracker.repository;

import org.inventory_tracker.entity.StationInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StationInventoryRepository extends JpaRepository<StationInventory,Long> {

//     @Query("""
//         SELECT s
//         FROM StationInventory s
//         WHERE s.outletId = :outletId
//         AND s.productType = :productType
//         AND s.businessDate = CURRENT_DATE
//     """)
//     Optional<StationInventory> findTodayInventory(@Param("outletId") String outletId, @Param("productType") ProductType productType);


Optional<StationInventory>
findByStation_IdAndStation_Merchant_CamsMerchantIdAndProduct_Id(
        Long stationId,
        String camsMerchantId,
        Long productId
);


@Query("""
    SELECT COUNT(DISTINCT si.product.id)
    FROM StationInventory si
    WHERE si.station.merchant.camsMerchantId = :merchantId
""")
long countDistinctProductsByMerchant(
        @Param("merchantId") String merchantId
);
    // long countDistinctProductByStation_Merchant_CamsMerchantId(String camsMerchantId);


    @Query("""
    SELECT COUNT(DISTINCT si.station.id)
    FROM StationInventory si
    WHERE si.product.id = :productId
      AND si.station.merchant.camsMerchantId = :merchantId
""")
long countDistinctStationsByProductIdAndMerchant(
        @Param("productId") Long productId,
        @Param("merchantId") String merchantId);
    // long countDistinctStationsByProductIdAndMerchant(Long productId, String merchantId);
    

    @Query("""
    SELECT COUNT(si)
    FROM StationInventory si
    WHERE si.station.merchant.camsMerchantId = :merchantId
      AND si.currentQuantity <= si.reorderLevel
""")
long countLowStockProductsByMerchant(
        @Param("merchantId") String merchantId
);
    // long countByStation_Merchant_CamsMerchantIdAndCurrentQuantityLessThanEqualReorderLevel(String camsMerchantId);

    
    @Query("""
        SELECT COUNT(DISTINCT si.station)
        FROM StationInventory si
        WHERE si.station.merchant.camsMerchantId = :merchantId
          AND si.currentQuantity <= si.reorderLevel
    """)
    long countDistinctStationsWithLowStockByMerchant(
        @Param("merchantId") String merchantId
    );

    List<StationInventory> findByStationIdAndStation_Merchant_CamsMerchantId(
        Long stationId,
        String merchantId);

    List<StationInventory> findByProductIdAndStation_Merchant_CamsMerchantId(
        Long productId,
        String merchantId);

    List<StationInventory> findByStation_Merchant_CamsMerchantId(String camsMerchantId);

    boolean existsByStationIdAndProductIdAndStation_Merchant_Id(Long stationId, Long productId, Long merchantId);

    Optional<StationInventory> findByIdAndStation_Merchant_Id(Long id, Long merchantId);

    Optional<StationInventory> findByStationIdAndProductIdAndStation_Merchant_Id(Long stationId, Long productId, Long merchantId);

    List<StationInventory> findByStationIdAndStation_Merchant_IdOrderByProduct_NameAsc(Long stationId, Long merchantId);

    List<StationInventory> findByStation_Merchant_IdOrderByStation_NameAscProduct_NameAsc(Long merchantId);

    List<StationInventory> findByActiveTrueAndStation_Merchant_IdOrderByStation_NameAsc(Long merchantId);

    Optional<StationInventory> findByStationIdAndProductId(Long stationId, Long productId);

    boolean existsByStationIdAndProductId(Long stationId, Long productId);

    List<StationInventory> findByStationIdOrderByProduct_NameAsc(Long stationId);

    List<StationInventory> findByProductIdOrderByStation_NameAsc(Long productId);

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
