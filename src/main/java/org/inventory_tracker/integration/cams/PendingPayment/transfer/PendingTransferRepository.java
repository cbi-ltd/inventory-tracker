package org.inventory_tracker.integration.cams.PendingPayment.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PendingTransferRepository extends JpaRepository<PendingTransfer, Long> {
        Optional<PendingTransfer> findByVirtualAccountNumberAndSaleNumber(String virtualAccountNumber, String saleNumber);
        Optional<PendingTransfer> findFirstByVirtualAccountNumberOrderByCreatedAtAsc(String virtualAccountNumber);
}
