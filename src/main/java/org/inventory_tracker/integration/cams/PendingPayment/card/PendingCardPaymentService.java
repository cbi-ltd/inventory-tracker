package org.inventory_tracker.integration.cams.PendingPayment.card;

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
public class PendingCardPaymentService {

    private final PendingCardPaymentRepository repository;

    /** How long a card payment stays open. After this it can no longer be matched and no longer blocks the terminal. */
    @Value("${cams.pending.card-ttl-minutes:10}")
    private int ttlMinutes;

    /** A terminal serves one customer at a time, so a second open payment on it is refused. */
    @Value("${cams.pending.single-open-per-terminal:true}")
    private boolean singleOpenPerTerminal;

    @Transactional
    public void register(String saleNumber, BigDecimal amount, String terminalSerialNumber, Long terminalId) {
        // registering the same sale again replaces its note, and expired notes never block the terminal
        repository.deleteBySaleNumber(saleNumber);
        repository.deleteByTerminalSerialNumberAndCreatedAtBefore(terminalSerialNumber, cutoff());
        if (singleOpenPerTerminal && !findOpen(terminalSerialNumber).isEmpty()) {
            throw new DuplicateResourceException(
                    "This terminal already has a card payment in progress. Wait for it to finish, or cancel that sale first.");
        }
        PendingCardPayment pending = PendingCardPayment.builder()
                .saleNumber(saleNumber)
                .amount(amount)
                .terminalId(terminalId)
                .terminalSerialNumber(terminalSerialNumber)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(pending);
    }

    /** The open card payments on a terminal, oldest first. Empty when there are none; it never throws. */
    @Transactional(readOnly = true)
    public List<PendingCardPayment> findOpen(String terminalSerialNumber) {
        return repository.findByTerminalSerialNumberAndCreatedAtAfterOrderByCreatedAtAsc(terminalSerialNumber, cutoff());
    }

    @Transactional
    public void deleteBySaleNumber(String saleNumber) {
        repository.deleteBySaleNumber(saleNumber);
    }

    @Transactional
    public void delete(PendingCardPayment pending) {
        repository.delete(pending);
    }

    private LocalDateTime cutoff() {
        return LocalDateTime.now().minusMinutes(ttlMinutes);
    }
}
