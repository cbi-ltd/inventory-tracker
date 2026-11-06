package org.inventory_tracker.integration.cams.PendingPayment.card;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingCardPaymentRepository extends JpaRepository<PendingCardPayment, Long> {
    Optional<PendingCardPayment> findFirstByTerminalSerialNumberAndAmount(String terminalSerial, BigDecimal amount);

    /** The open (not yet expired) card payments on one terminal, oldest first. */
    List<PendingCardPayment> findByTerminalSerialNumberAndCreatedAtAfterOrderByCreatedAtAsc(String terminalSerialNumber, LocalDateTime cutoff);

    void deleteByTerminalSerialNumberAndCreatedAtBefore(String terminalSerialNumber, LocalDateTime cutoff);

    void deleteBySaleNumber(String saleNumber);
}
