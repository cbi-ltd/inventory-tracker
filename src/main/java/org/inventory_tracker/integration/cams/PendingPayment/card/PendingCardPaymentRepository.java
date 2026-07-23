package org.inventory_tracker.integration.cams.PendingPayment.card;

import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.Optional;

public interface PendingCardPaymentRepository extends JpaRepository<PendingCardPayment, Long> {

    Optional<PendingCardPayment> findFirstByTerminalIdAndAmount(String terminalId, BigDecimal amount);
}
