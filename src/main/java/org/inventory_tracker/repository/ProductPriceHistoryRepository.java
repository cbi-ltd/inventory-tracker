package org.inventory_tracker.repository;

import org.inventory_tracker.entity.ProductPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long>, JpaSpecificationExecutor<ProductPriceHistory> {
    List<ProductPriceHistory>findByChangedByAndStation_Merchant_IdOrderByChangedAtDesc(String changedBy, Long merchantId);
    
    List<ProductPriceHistory>findByBusinessDateBetweenAndStation_Merchant_IdOrderByChangedAtDesc(LocalDate startDate, LocalDate endDate, Long merchantId);
    
    List<ProductPriceHistory>findByBusinessDateAndStation_Merchant_IdOrderByChangedAtDesc(LocalDate businessDate, Long merchantId);

    List<ProductPriceHistory>findByProductIdAndStation_Merchant_IdOrderByChangedAtDesc(Long productId, Long merchantId);
    
    List<ProductPriceHistory>findByStationIdOrderByChangedAtDesc(Long stationId);

    List<ProductPriceHistory>findByProductIdOrderByChangedAtDesc(Long productId);

    List<ProductPriceHistory>findByStationIdAndProductIdOrderByChangedAtDesc(Long stationId, Long productId);

    List<ProductPriceHistory>findByBusinessDateOrderByChangedAtDesc(LocalDate businessDate);

    List<ProductPriceHistory>findByBusinessDateBetweenOrderByChangedAtDesc(LocalDate startDate, LocalDate endDate);

    List<ProductPriceHistory>findAllByOrderByChangedAtDesc();

    List<ProductPriceHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
}
