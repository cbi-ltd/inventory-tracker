package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Delivery;
import org.inventory_tracker.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    boolean existsByDeliveryNumber(String deliveryNumber);

//     List<Delivery> findByStationIdOrderByReceivedAtDesc(
//             Long stationId
//     );
    List<Delivery> findByStationIdOrderByBusinessDateDescReceivedAtDesc(
            Long stationId
    );
//     List<Delivery> findByProductIdOrderByReceivedAtDesc(
//             Long productId
//     );
    List<Delivery> findByProductIdOrderByBusinessDateDescReceivedAtDesc(
            Long productId
    );

    List<Delivery> findByStationInventoryIdOrderByReceivedAtDesc(
            Long stationInventoryId
    );

//     List<Delivery> findByStatusOrderByReceivedAtDesc(
//             DeliveryStatus status
//     );
    List<Delivery> findByStatusOrderByBusinessDateDescReceivedAtDesc(
            DeliveryStatus status
    );

    List<Delivery> findByBusinessDateBetweenOrderByReceivedAtDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Delivery> findByStationIdAndBusinessDateBetweenOrderByReceivedAtDesc(
            Long stationId,
            LocalDate startDate,
            LocalDate endDate
    );

//     List<Delivery> findAllByOrderByReceivedAtDesc();
    List<Delivery> findAllByOrderByBusinessDateDescReceivedAtDesc();

    long countByStationId(Long stationId);

    long countByBusinessDate(LocalDate businessDate);

    long countByProductId(Long productId);

//     long countByReceivedBy(String receivedBy);
}
