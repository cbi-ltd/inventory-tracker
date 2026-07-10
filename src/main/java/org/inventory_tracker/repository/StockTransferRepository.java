package org.inventory_tracker.repository;

import org.inventory_tracker.entity.StockTransfer;
import org.inventory_tracker.enums.StockTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {

    Optional<StockTransfer> findByTransferNumber(
            String transferNumber
    );


    boolean existsByTransferNumber(
            String transferNumber
    );


    List<StockTransfer>
    findAllByOrderByBusinessDateDescCompletedAtDesc();


    List<StockTransfer>
    findByStatusOrderByBusinessDateDescCompletedAtDesc(
            StockTransferStatus status
    );


    List<StockTransfer>
    findByBusinessDateOrderByCompletedAtDesc(
            LocalDate businessDate
    );


    List<StockTransfer>
    findByBusinessDateBetweenOrderByBusinessDateDescCompletedAtDesc(
            LocalDate startDate,
            LocalDate endDate
    );


    List<StockTransfer>
    findBySourceStationIdOrderByBusinessDateDescCompletedAtDesc(
            Long stationId
    );


    List<StockTransfer>
    findByDestinationStationIdOrderByBusinessDateDescCompletedAtDesc(
            Long stationId
    );


    List<StockTransfer>
    findByProductIdOrderByBusinessDateDescCompletedAtDesc(
            Long productId
    );


    List<StockTransfer>
    findBySourceInventoryIdOrderByBusinessDateDescCompletedAtDesc(
            Long stationInventoryId
    );


    List<StockTransfer>
    findByDestinationInventoryIdOrderByBusinessDateDescCompletedAtDesc(
            Long stationInventoryId
    );

    @Query("""
       SELECT st
       FROM StockTransfer st
       WHERE st.sourceStation.id = :stationId
       AND st.status = org.inventory_tracker.enums.StockTransferStatus.PENDING
       ORDER BY st.businessDate DESC
       """)
    List<StockTransfer> findPendingTransfersBySourceStation(Long stationId);

    long countBySourceStationId(Long stationId);

    long countByDestinationStationId(Long stationId);

    long countByBusinessDate(LocalDate businessDate);

    long countByProductId(Long productId);

    long countByInitiatedBy(String initiatedBy);

}
