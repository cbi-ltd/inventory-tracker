package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.inventory_tracker.enums.InventoryTransactionType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateInventoryTransactionRequest {

    @NotNull
    private Long stationInventoryId;

    @NotNull
    private InventoryTransactionType transactionType;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal quantity;

    private String remarks;
}
