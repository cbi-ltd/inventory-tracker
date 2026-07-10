package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.enums.StockAdjustmentType;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateStockAdjustmentRequest {

    @NotNull(message = "Station inventory is required")
    private Long stationInventoryId;

    @NotNull(message = "Adjustment type is required")
    private StockAdjustmentType adjustmentType;

    @NotNull(message = "Adjustment reason is required")
    private StockAdjustmentReason reason;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private String adjustedBy;

    private String remarks;
}
