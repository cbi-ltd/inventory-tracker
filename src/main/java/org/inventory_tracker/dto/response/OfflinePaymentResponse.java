package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class OfflinePaymentResponse {
    private Long id;
    private String reference;
    private Long saleId;
    private Long stationId;
    private BigDecimal amount;
    private PaymentStatus status;

    private String bankName;
    private String accountNumber;
    private String accountName;

    private String customerTransferReference;
    private LocalDateTime receivedAt;
}