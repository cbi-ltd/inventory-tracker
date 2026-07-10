package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateDeliveryRequest {

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001")
    private BigDecimal quantityDelivered;

    private String remarks;
}
