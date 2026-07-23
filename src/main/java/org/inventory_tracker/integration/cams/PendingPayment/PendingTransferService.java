package org.inventory_tracker.integration.cams.PendingPayment;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class PendingTransferService {

    private final PendingTransferRepository pendingTransferRepository;
    

    @Transactional
    public void registerPendingTransfer(String virtualAccountNumber, String saleNumber, BigDecimal amount, String deviceSerial) {

        PendingTransfer pendingTransfer = PendingTransfer.builder()
                .virtualAccountNumber(virtualAccountNumber)
                .saleNumber(saleNumber)
                .amount(amount)
                .deviceSerial(deviceSerial)
                .build();

        pendingTransferRepository.save(pendingTransfer);
    }

    @Transactional
    public void registerPendingTransfer(
            String virtualAccountNumber,
            String saleNumber,
            BigDecimal amount,
            String tid,
            String deviceSerial) {

        PendingTransfer pendingTransfer = PendingTransfer.builder()
                .virtualAccountNumber(virtualAccountNumber)
                .saleNumber(saleNumber)
                .amount(amount)
                .tid(tid)
                .deviceSerial(deviceSerial)
                .build();

        pendingTransferRepository.save(pendingTransfer);
    }

    @Transactional
    public PendingTransfer find(String virtualAccountNumber) {
        // PendingTransfer pending = pendingTransferRepository.findByVirtualAccountNumberAndSaleNumber(virtualAccountNumber, saleNumber)
        //                             .orElseThrow(() -> new EntityNotFoundException("No pending transfer found for account: " + virtualAccountNumber));
        PendingTransfer pending = pendingTransferRepository.findFirstByVirtualAccountNumberOrderByCreatedAtAsc(virtualAccountNumber)
                                    .orElseThrow(() -> new EntityNotFoundException("No pending transfer found for account: " + virtualAccountNumber));

        return pending;
    }

    public void delete(PendingTransfer pendingTransfer) {
        pendingTransferRepository.delete(pendingTransfer);
    }
}
