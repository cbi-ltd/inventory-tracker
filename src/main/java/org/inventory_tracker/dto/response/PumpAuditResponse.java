package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PumpAuditResponse {

    private Long id;

    private Integer pumpNumber;

    private Double openingReading;

    private Double closingReading;

    private Double totalDispensed;

    private Long stationId;

    private String stationName;

    private Long attendantId;

    private String attendantName;

    private Long posTerminalId;

    private String terminalSerialNumber;
}