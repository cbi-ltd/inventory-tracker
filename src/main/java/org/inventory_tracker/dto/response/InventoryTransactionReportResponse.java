package org.inventory_tracker.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.InventoryTransactionType;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionReportResponse {

    private Long transactionId;

    private Long stationId;
    private String stationName;

    private Long stationInventoryId;

    private Long productId;
    private String productName;

    private InventoryTransactionType transactionType;

    private BigDecimal quantity;

    private BigDecimal balanceBeforeTransaction;

    private BigDecimal balanceAfterTransaction;

    private String remarks;

    private String referenceNumber;

    private LocalDate businessDate;

    private LocalDateTime transactionTime;
}
