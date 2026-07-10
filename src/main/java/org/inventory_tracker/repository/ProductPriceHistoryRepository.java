package org.inventory_tracker.repository;

import org.inventory_tracker.entity.ProductPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ProductPriceHistoryRepository
        extends JpaRepository<ProductPriceHistory, Long> {

    List<ProductPriceHistory>
    findByStationIdOrderByChangedAtDesc(
            Long stationId
    );

    List<ProductPriceHistory>
    findByProductIdOrderByChangedAtDesc(
            Long productId
    );

    List<ProductPriceHistory>
    findByStationIdAndProductIdOrderByChangedAtDesc(
            Long stationId,
            Long productId
    );

    List<ProductPriceHistory>
    findByBusinessDateOrderByChangedAtDesc(
            LocalDate businessDate
    );

    List<ProductPriceHistory>
    findByBusinessDateBetweenOrderByChangedAtDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    List<ProductPriceHistory>
    findAllByOrderByChangedAtDesc();
}
