package org.inventory_tracker.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.entity.Pump;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePumpAuditRequest {

    private Pump pump;
    private Double openingReading;
    private Double closingReading;
    private Long stationId;
    private Long attendantId;
    private Long posTerminalId;
}