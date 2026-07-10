package org.inventory_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.enums.StockAdjustmentType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentResponse {

    private Long id;

    private String adjustmentNumber;

    private LocalDate businessDate;

    private Long stationInventoryId;

    private Long stationId;
    private String stationName;

    private Long productId;
    private String productName;

    private Long stockCountId;
    private String stockCountNumber;

    private StockAdjustmentType adjustmentType;

    private StockAdjustmentReason reason;

    private BigDecimal quantity;

    private BigDecimal balanceBeforeAdjustment;

    private BigDecimal balanceAfterAdjustment;

    private BigDecimal unitPrice;

    private BigDecimal totalAdjustmentValue;

    private String adjustedBy;

    private String remarks;
}
