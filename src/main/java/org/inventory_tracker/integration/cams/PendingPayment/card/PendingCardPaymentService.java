package org.inventory_tracker.integration.cams.PendingPayment.card;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PendingCardPaymentService {

    private final PendingCardPaymentRepository repository;

    @Transactional
    public void register(String saleNumber, BigDecimal amount, String terminalSerialNumber, String terminalId) {

        PendingCardPayment pending = PendingCardPayment.builder()
                .saleNumber(saleNumber)
                .amount(amount)
                .terminalId(terminalId)
                .terminalSerialNumber(terminalSerialNumber)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(pending);
    }


    @Transactional
    public PendingCardPayment consume(String terminalId, BigDecimal amount) {

        return repository.findFirstByTerminalIdAndAmount(terminalId, amount)
                .map(payment -> {
                    repository.delete(payment);
                    return payment;
                })
                .orElseThrow(() -> new ResourceNotFoundException("No Pending Payment For Terminal "+ terminalId));
    }
}
