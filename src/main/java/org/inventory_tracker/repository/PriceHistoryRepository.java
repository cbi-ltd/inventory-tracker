// package org.inventory_tracker.repository;


// import org.inventory_tracker.entity.PriceHistory;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.time.LocalDateTime;
// import java.util.List;

// public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

//     List<PriceHistory>
//     findByStationInventoryIdOrderByEffectiveDateDesc(
//             Long stationInventoryId);

//     List<PriceHistory>
//     findByStationInventoryStationIdOrderByEffectiveDateDesc(
//             Long stationId);

//     List<PriceHistory>
//     findByStationInventoryProductIdOrderByEffectiveDateDesc(
//             Long productId);

//     List<PriceHistory>
//     findByChangedByOrderByEffectiveDateDesc(
//             String changedBy);

//     List<PriceHistory>
//     findByEffectiveDateBetweenOrderByEffectiveDateDesc(
//             LocalDateTime start,
//             LocalDateTime end);
// }
