package org.inventory_tracker.repository;

import org.inventory_tracker.entity.StockCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockCountRepository
        extends JpaRepository<StockCount, Long> {

    Optional<StockCount> findByCountNumber(
            String countNumber);

    List<StockCount>
    findAllByOrderByBusinessDateDescCreatedAtDesc();

    List<StockCount>
    findByStationIdOrderByBusinessDateDescCreatedAtDesc(
            Long stationId);

    List<StockCount>
    findByProductIdOrderByBusinessDateDescCreatedAtDesc(
            Long productId);

    List<StockCount>
    findByStationInventoryIdOrderByBusinessDateDescCreatedAtDesc(
            Long stationInventoryId);

    List<StockCount>
    findByBusinessDateBetweenOrderByBusinessDateDescCreatedAtDesc(
            LocalDate startDate,
            LocalDate endDate);

    long countByStationId(Long stationId);

    long countByBusinessDate(LocalDate businessDate);

    long countByCountedBy(String countedBy);

    long countByProductId(Long productId);
}
