package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AssignPumpRequest {

    @NotNull(message = "Attendant ID is required")
    private Long attendantId;

    @NotNull(message = "Pump ID is required")
    private Long pumpId;

    @NotNull(message = "Station ID is required")
    private Long stationId;
}
