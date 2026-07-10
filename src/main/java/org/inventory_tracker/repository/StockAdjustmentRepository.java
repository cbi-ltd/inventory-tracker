package org.inventory_tracker.repository;


import org.inventory_tracker.entity.StockAdjustment;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockAdjustmentRepository
        extends JpaRepository<StockAdjustment, Long> {

    Optional<StockAdjustment> findByAdjustmentNumber(
            String adjustmentNumber);

    List<StockAdjustment>
    findAllByOrderByBusinessDateDescCreatedAtDesc();

    List<StockAdjustment>
    findByStationIdOrderByBusinessDateDescCreatedAtDesc(
            Long stationId);

    List<StockAdjustment>
    findByProductIdOrderByBusinessDateDescCreatedAtDesc(
            Long productId);

    List<StockAdjustment>
    findByStationInventoryIdOrderByBusinessDateDescCreatedAtDesc(
            Long stationInventoryId);

    List<StockAdjustment>
    findByReasonOrderByBusinessDateDescCreatedAtDesc(
            StockAdjustmentReason reason);

    List<StockAdjustment>
    findByBusinessDateBetweenOrderByBusinessDateDescCreatedAtDesc(
            LocalDate startDate,
            LocalDate endDate);

    long countByStationId(Long stationId);

    long countByBusinessDate(LocalDate businessDate);

    long countByProductId(Long productId);

    long countByAdjustedBy(String adjustedBy);
}
