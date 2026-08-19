package org.inventory_tracker.dto.request;

import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;



@Getter
@Setter
public class ClosePumpAuditRequest {

    @NonNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal closingReading;

    @NonNull
    private String terminalSerialNumber;
}
