package org.inventory_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.inventory_tracker.entity.OfflinePaymentReconciliation;
import java.util.Optional;
import java.util.List;

public interface OfflinePaymentReconciliationRepository extends JpaRepository<OfflinePaymentReconciliation, Long> {
    
    Optional<OfflinePaymentReconciliation> findByIdAndMerchant_Id(Long id, Long merchantId);

    List<OfflinePaymentReconciliation> findByStation_IdAndMerchant_IdOrderByInitiatedAtDesc(Long stationId, Long merchantId);

    boolean existsByTransferReferenceAndMerchant_Id(String transferReference, Long merchantId);
}
