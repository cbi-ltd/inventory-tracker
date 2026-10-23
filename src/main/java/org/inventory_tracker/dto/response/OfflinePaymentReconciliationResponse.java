package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import lombok.*;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.ReconciliationStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflinePaymentReconciliationResponse {

    private Long id;
    private String reference;
    private Long offlinePaymentId;
    private String paymentReference;
    private BigDecimal amount;
    private String transferReference;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private ReconciliationStatus status;
    private LocalDateTime initiatedAt;
    private LocalDateTime confirmedAt;
}
