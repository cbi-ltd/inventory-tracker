package org.inventory_tracker.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.inventory_tracker.enums.Shift;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PumpAuditResponse {

    private Long id;

    private Long pumpAssignmentId;

    private Long pumpId;

    private Integer pumpNumber;

    private BigDecimal openingReading;

    private BigDecimal closingReading;

    private BigDecimal totalDispensed;

    private LocalDate assignmentDate;

    private Shift shift;

    private Long stationId;

    private String stationName;

    private Long attendantId;

    private String attendantName;

    private LocalDateTime clockInTime;

    private LocalDateTime clockOutTime;

    private Long terminalDbId;

    private Long tid;

    private String terminalSerialNumber;

    private LocalDate businessDate;

    private String remarks;
}