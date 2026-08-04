package org.inventory_tracker.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
public class PumpAuditFilterRequest {

    private Long id;

    private Long stationId;

    private Long pumpId;

    private Long attendantId;

    private Long assignmentId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate businessDate;
}
