package org.inventory_tracker.entity;

import java.math.BigDecimal;
import java.util.List;
import org.inventory_tracker.entity.PosAssignmentResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.enums.TerminalMode;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosSessionResponse {

    private Long stationId;
    private String stationName;

    private Long terminalId;
    private String terminalSerialNumber;
    private String tid;

    private Shift shift;
    private LocalDate businessDate;

    private TerminalMode terminalMode;
    private List<PosAssignmentResponse> assignments;
}
