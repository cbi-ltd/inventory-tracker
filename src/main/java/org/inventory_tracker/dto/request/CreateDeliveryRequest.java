package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class CreateDeliveryRequest {

    @NotBlank(message = "Delivery number is required")
    private String deliveryNumber;

    @NotBlank(message = "Supplier name is required")
    private String supplierName;

    @NotNull(message = "Station inventory is required")
    private Long stationInventoryId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.001")
    private BigDecimal quantityDelivered;

    private String remarks;
}
