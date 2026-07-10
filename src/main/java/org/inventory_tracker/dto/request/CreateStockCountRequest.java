package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateStockCountRequest {

    @NotNull(message = "Station inventory is required")
    private Long stationInventoryId;

    @NotNull(message = "Counted quantity is required")
    @DecimalMin(value = "0.000", inclusive = true,
            message = "Counted quantity cannot be negative")
    private BigDecimal countedQuantity;

    @NotNull(message = "Counted by is required")
    private String countedBy;

    private String remarks;
}
