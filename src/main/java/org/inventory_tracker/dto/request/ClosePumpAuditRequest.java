package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.inventory_tracker.util.ValidTerminalInfo;
import jakarta.validation.constraints.DecimalMin;



@Getter
@Setter
@ValidTerminalInfo
public class ClosePumpAuditRequest {

    @NotNull(message = "Closing reading is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Closing reading cannot be negative")
    private BigDecimal closingReading;

    private Long pumpId;


    private String terminalSerialNumber;

    private  Long pumpAssignmentId;
}
