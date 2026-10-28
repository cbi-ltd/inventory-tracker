package org.inventory_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.inventory_tracker.entity.OfflinePayment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.inventory_tracker.enums.PaymentStatus;


public interface OfflinePaymentRepository extends JpaRepository<OfflinePayment, Long> {

    Optional<OfflinePayment> findByIdAndMerchant_Id(Long id, Long merchantId);

    Optional<OfflinePayment> findByReferenceAndMerchant_Id(String reference, Long merchantId);

    Page<OfflinePayment> findByStation_IdAndMerchant_IdAndStatus(
            Long stationId,
            Long merchantId,
            PaymentStatus status,
            Pageable pageable);

    Page<OfflinePayment> findByStation_IdAndMerchant_Id(Long stationId, Long merchantId, Pageable pageable);

    boolean existsBySale_Id(Long saleId);

    Optional<OfflinePayment> findBySale_Id(Long saleId);

    Page<OfflinePayment> findAllByMerchant_Id(Long merchantId, Pageable pageable);

}