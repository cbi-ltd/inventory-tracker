package org.inventory_tracker.entity;

import java.math.BigDecimal;
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

    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    private Long productId;
    private String productName;

    private Long attendantId;
    private String attendantName;

    private Shift shift;
    private LocalDate businessDate;

    private BigDecimal openingReading;
    private BigDecimal closingReading;
    private Long pumpAuditId;
    private TerminalMode terminalMode;
}
