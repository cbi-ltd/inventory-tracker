package org.inventory_tracker.entity;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import org.inventory_tracker.enums.Shift;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosSessionResponse {

    // Station
    private Long stationId;
    private String stationName;

    // Terminal
    private Long terminalId;
    private String terminalSerialNumber;
    private String tid;

    // Pump
    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    // Product
    private Long productId;
    private String productName;

    // Attendant
    private Long attendantId;
    private String attendantName;

    // Assignment
    private Shift shift;
    private LocalDate businessDate;

    // Meter
    private BigDecimal openingReading;
    private BigDecimal closingReading;
    private Long pumpAuditId;
}
