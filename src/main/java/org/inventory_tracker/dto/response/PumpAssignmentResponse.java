package org.inventory_tracker.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PumpAssignmentResponse {

    private Long id;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpName;
    private String pumpNumber;

    private Long attendantId;
    private String attendantName;
    private String username;
    private Long terminalDbId;
    private String terminalId;
    private String terminalSerialNumber;
    // private String actualTerminalId;
    // private String actualTerminalSerialNumber;

    private LocalDate assignmentDate;

    private Shift shift;

    private Boolean active;

    private LocalDateTime createdAt;
}
