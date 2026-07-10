package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class UpdateStationInventoryRequest {

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.00")
    private BigDecimal sellingPrice;

    @NotNull(message = "Reorder level is required")
    @DecimalMin(value = "0.00")
    private BigDecimal reorderLevel;
}
