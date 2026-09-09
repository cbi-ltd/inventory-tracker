package org.inventory_tracker.dto.request;

import lombok.*;
import java.math.BigDecimal;

import org.inventory_tracker.util.ValidTerminalInfo;

import jakarta.validation.constraints.DecimalMin;



@Getter
@Setter
@ValidTerminalInfo
public class ClosePumpAuditRequest {

    @NonNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal closingReading;

    private String terminalSerialNumber;

    private  Long pumpAssignmentId;
}
