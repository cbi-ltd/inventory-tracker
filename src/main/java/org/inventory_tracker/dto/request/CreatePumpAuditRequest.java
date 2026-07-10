package org.inventory_tracker.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePumpAuditRequest {


    @NotNull(message = "Pump assignment is required")
    private Long pumpAssignmentId;

    @NotNull(message = "Opening meter reading is required")
    private Double openingMeterReading;

    private String remarks;

    // private Pump pump;
    // private Double openingReading;
    // private Double closingReading;
    // private Long stationId;
    // private Long attendantId;
    // private Long posTerminalId;
}
