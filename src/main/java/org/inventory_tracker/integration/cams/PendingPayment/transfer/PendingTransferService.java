package org.inventory_tracker.integration.cams.PendingPayment.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PendingTransferService {

    private final PendingTransferRepository pendingTransferRepository;

    /** How long a transfer payment stays open. After this it can no longer be matched and no longer blocks the terminal. */
    @Value("${cams.pending.transfer-ttl-minutes:15}")
    private int ttlMinutes;

    /** A terminal serves one customer at a time, so a second open payment on it is refused. */
    @Value("${cams.pending.single-open-per-terminal:true}")
    private boolean singleOpenPerTerminal;

    @Transactional
    public void registerPendingTransfer(String virtualAccountNumber, String saleNumber, BigDecimal amount, String deviceSerial) {
        registerPendingTransfer(virtualAccountNumber, saleNumber, amount, deviceSerial, deviceSerial);
    }

    @Transactional
    public void registerPendingTransfer(
            String virtualAccountNumber,
            String saleNumber,
            BigDecimal amount,
            String tid,
            String deviceSerial) {
        // registering the same sale again replaces its note, and expired notes never block the terminal
        pendingTransferRepository.deleteBySaleNumber(saleNumber);
        pendingTransferRepository.deleteByDeviceSerialAndCreatedAtBefore(deviceSerial, cutoff());
        if (singleOpenPerTerminal && !findOpen(deviceSerial).isEmpty()) {
            throw new DuplicateResourceException(
                    "This terminal already has a transfer payment in progress. Wait for it to finish, or cancel that sale first.");
        }
        PendingTransfer pendingTransfer = PendingTransfer.builder()
                .virtualAccountNumber(virtualAccountNumber)
                .saleNumber(saleNumber)
                .amount(amount)
                .tid(tid)
                .deviceSerial(deviceSerial)
                .build();
        pendingTransferRepository.save(pendingTransfer);
    }

    /** The open transfer payments on a terminal, oldest first. Empty when there are none; it never throws. */
    @Transactional(readOnly = true)
    public List<PendingTransfer> findOpen(String deviceSerial) {
        return pendingTransferRepository.findByDeviceSerialAndCreatedAtAfterOrderByCreatedAtAsc(deviceSerial, cutoff());
    }

    @Transactional
    public void deleteBySaleNumber(String saleNumber) {
        pendingTransferRepository.deleteBySaleNumber(saleNumber);
    }

    public void delete(PendingTransfer pendingTransfer) {
        pendingTransferRepository.delete(pendingTransfer);
    }

    private LocalDateTime cutoff() {
        return LocalDateTime.now().minusMinutes(ttlMinutes);
    }
}
