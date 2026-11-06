package org.inventory_tracker.integration.cams.PendingPayment.transfer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingTransferRepository extends JpaRepository<PendingTransfer, Long> {
        Optional<PendingTransfer> findByVirtualAccountNumberAndSaleNumber(String virtualAccountNumber, String saleNumber);

        Optional<PendingTransfer> findFirstByVirtualAccountNumberOrderByCreatedAtAsc(String virtualAccountNumber);

        /** The open (not yet expired) transfer payments on one terminal, oldest first. */
        List<PendingTransfer> findByDeviceSerialAndCreatedAtAfterOrderByCreatedAtAsc(String deviceSerial, LocalDateTime cutoff);

        void deleteByDeviceSerialAndCreatedAtBefore(String deviceSerial, LocalDateTime cutoff);

        void deleteBySaleNumber(String saleNumber);
}
