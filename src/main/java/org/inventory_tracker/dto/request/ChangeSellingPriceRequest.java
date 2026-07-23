package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChangeSellingPriceRequest {

    @NotNull(message = "Station Inventory ID is required")
    private Long stationInventoryId;

    @NotNull(message = "New selling price is required")
    @DecimalMin(
            value = "0.01",
            message = "Selling price must be greater than zero"
    )
    private BigDecimal newSellingPrice;

    @NotBlank(message = "Changed by is required")
    private String changedBy;

    @NotBlank(message = "Reason is required")
    private String reason;

    private LocalDateTime effectiveDate;
}
