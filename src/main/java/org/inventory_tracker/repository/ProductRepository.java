package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Payment;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
    SELECT COUNT(DISTINCT si.product)
    FROM StationInventory si
    WHERE si.station.merchant.camsMerchantId = :camsMerchantId
""")
long countDistinctProductsByMerchant(
        @Param("camsMerchantId") String camsMerchantId
);

    // long countDistinctProductByStation_Merchant_CamsMerchantId(
    //     String merchantId);

        @Query("""
    SELECT DISTINCT si.product
    FROM StationInventory si
    WHERE si.station.merchant.camsMerchantId = :camsMerchantId
    ORDER BY si.product.name ASC
""")
List<Product> findProductsByMerchant(
        @Param("camsMerchantId") String camsMerchantId
);

    // List<Product> findByStationInventories_Station_Merchant_CamsMerchantId(String camsMerchantId);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByProductType(ProductType productType);

    Optional<Product> findByProductType(ProductType productType);

    List<Product> findAllByOrderByNameAsc();

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findByProductTypeOrderByNameAsc(ProductType productType);

    Optional<Product> findByNameIgnoreCase(String name);
}
