package org.inventory_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.inventory_tracker.entity.OfflinePaymentReconciliation;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OfflinePaymentReconciliationRepository extends JpaRepository<OfflinePaymentReconciliation, Long> {
    
    Optional<OfflinePaymentReconciliation> findByIdAndMerchant_Id(Long id, Long merchantId);

    List<OfflinePaymentReconciliation> findByStation_IdAndMerchant_IdOrderByInitiatedAtDesc(Long stationId, Long merchantId);

    boolean existsByTransferReferenceAndMerchant_Id(String transferReference, Long merchantId);

    Page<OfflinePaymentReconciliation> findAllByMerchant_Id(Long merchantId, Pageable pageable);

    boolean existsByReference(String reference);
}
