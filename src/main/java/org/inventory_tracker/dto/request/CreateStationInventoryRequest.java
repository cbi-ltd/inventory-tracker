package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
public class CreateStationInventoryRequest {

    @NotNull(message = "Station is required")
    private Long stationId;

    @NotNull(message = "Product is required")
    private Long productId;

    // @NotNull
    // private BigDecimal costPerUnit;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.00")
    private BigDecimal sellingPrice;

    // @NotNull(message = "Reorder level is required")
    // @DecimalMin(value = "0.00")
    // private BigDecimal reorderLevel;

    @DecimalMin(value = "0.00")
    private BigDecimal currentQuantity = BigDecimal.ZERO;
}