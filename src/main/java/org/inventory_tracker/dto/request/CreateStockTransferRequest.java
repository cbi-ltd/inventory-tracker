package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class CreateStockTransferRequest {

    @NotNull(message = "Source inventory is required.")
    private Long sourceInventoryId;

    @NotNull(message = "Destination inventory is required.")
    private Long destinationInventoryId;

    @NotNull(message = "Quantity is required.")
    @DecimalMin(
            value = "0.001",
            message = "Quantity must be greater than zero."
    )
    private BigDecimal quantityTransferred;

    private String remarks;

}
