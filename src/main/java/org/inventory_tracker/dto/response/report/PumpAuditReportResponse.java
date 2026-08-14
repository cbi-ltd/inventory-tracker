package org.inventory_tracker.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.Shift;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PumpAuditReportResponse {

    private Long auditId;

    private Long assignmentId;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    private Long attendantId;
    private String attendantName;

    private LocalDate businessDate;
    private Shift shift;

    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    private BigDecimal openingReading;
    private BigDecimal closingReading;
    private BigDecimal totalDispensed;
}
