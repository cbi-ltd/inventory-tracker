package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Delivery;
import org.inventory_tracker.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {
    long countByProductIdAndStation_Merchant_CamsMerchantId(
        Long productId,
        String merchantId);
    
    long countByStation_Merchant_CamsMerchantIdAndBusinessDate(
        String merchantId,
        LocalDate businessDate);
    
    List<Delivery>findByStation_Merchant_CamsMerchantIdOrderByBusinessDateDescReceivedAtDesc(String merchantId);
    
    Optional<Delivery> findByIdAndStation_Merchant_Id(Long deliveryId, Long merchantId);

    Optional<Delivery> findByDeliveryNumberAndStation_Merchant_Id(String deliveryNumber, Long merchantId);
    
    List<Delivery> findByStation_Merchant_IdOrderByBusinessDateDescReceivedAtDesc(Long merchantId);

    List<Delivery> findByStation_Merchant_IdAndStation_IdOrderByBusinessDateDescReceivedAtDesc(Long merchantId, Long stationId);

    List<Delivery> findByStation_Merchant_IdAndProduct_IdOrderByBusinessDateDescReceivedAtDesc(Long merchantId, Long productId);

    List<Delivery> findByStation_Merchant_IdAndStationInventory_IdOrderByReceivedAtDesc(Long merchantId, Long stationInventoryId);

    List<Delivery> findByStation_Merchant_IdAndStatusOrderByBusinessDateDescReceivedAtDesc(Long merchantId, DeliveryStatus status);

    List<Delivery> findByStation_Merchant_IdAndBusinessDateBetweenOrderByReceivedAtDesc(Long merchantId, LocalDate startDate, LocalDate endDate);

    List<Delivery> findByStation_Merchant_IdAndStation_IdAndBusinessDateBetweenOrderByReceivedAtDesc(Long merchantId, Long stationId, LocalDate startDate, LocalDate endDate);
    
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    boolean existsByDeliveryNumber(String deliveryNumber);

    List<Delivery> findByStationIdOrderByReceivedAtDesc(Long stationId);

    List<Delivery> findByStationIdOrderByBusinessDateDescReceivedAtDesc(Long stationId);

    List<Delivery> findByProductIdOrderByReceivedAtDesc(Long productId);

    List<Delivery> findByProductIdOrderByBusinessDateDescReceivedAtDesc(Long productId);

    List<Delivery> findByStationInventoryIdOrderByReceivedAtDesc(Long stationInventoryId);

    List<Delivery> findByStatusOrderByReceivedAtDesc(DeliveryStatus status);

    List<Delivery> findByStatusOrderByBusinessDateDescReceivedAtDesc(DeliveryStatus status);

    List<Delivery> findByBusinessDateBetweenOrderByReceivedAtDesc(LocalDate startDate, LocalDate endDate);

    List<Delivery> findByStationIdAndBusinessDateBetweenOrderByReceivedAtDesc(Long stationId, LocalDate startDate, LocalDate endDate);

    List<Delivery> findAllByOrderByReceivedAtDesc();

    List<Delivery> findAllByOrderByBusinessDateDescReceivedAtDesc();

    long countByStationId(Long stationId);

    long countByBusinessDate(LocalDate businessDate);

    long countByProductId(Long productId);

    // long countByReceivedBy(String receivedBy);
}
