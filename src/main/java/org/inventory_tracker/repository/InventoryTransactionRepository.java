package org.inventory_tracker.repository;

import org.inventory_tracker.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;




@Repository
public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction>
    findByStationInventoryIdOrderByTransactionTimeDesc(
            Long stationInventoryId);

    List<InventoryTransaction>
    findByStationIdOrderByTransactionTimeDesc(
            Long stationId);

    List<InventoryTransaction>
    findByProductIdOrderByTransactionTimeDesc(
            Long productId);

    List<InventoryTransaction>
    findByBusinessDateOrderByTransactionTimeDesc(
            LocalDate businessDate);

    List<InventoryTransaction>
    findByBusinessDateBetweenOrderByTransactionTimeDesc(
            LocalDate startDate,
            LocalDate endDate);

    List<InventoryTransaction>
    findByStationIdAndBusinessDateBetweenOrderByTransactionTimeDesc(
            Long stationId,
            LocalDate startDate,
            LocalDate endDate);

    List<InventoryTransaction>
    findByProductIdAndBusinessDateBetweenOrderByTransactionTimeDesc(
            Long productId,
            LocalDate startDate,
            LocalDate endDate);

    List<InventoryTransaction>
    findByStationIdAndProductIdOrderByTransactionTimeDesc(
            Long stationId,
            Long productId);
}
