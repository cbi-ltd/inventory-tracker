package org.inventory_tracker.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.Shift;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PumpAssignmentReportResponse {

    private Long assignmentId;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    private Long attendantId;
    private String attendantName;

    private Long terminalId;
    private String terminalSerialNumber;

    private LocalDate assignmentDate;
    private Shift shift;

    private Boolean active;
}
