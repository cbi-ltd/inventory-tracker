package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.InventoryTransactionType;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {

    private Long id;

    private Long stationInventoryId;

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private InventoryTransactionType transactionType;

    private String referenceNumber;

    private BigDecimal quantity;

    private BigDecimal balanceBeforeTransaction;

    private BigDecimal balanceAfterTransaction;

    private String remarks;

    private LocalDate businessDate;

    private LocalDateTime transactionTime;
}
