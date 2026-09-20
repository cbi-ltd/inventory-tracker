package org.inventory_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.Shift;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftSummaryResponse {

    private Long stationId;

    private String stationName;

    private Long pumpId;

    private String pumpNumber;

    private String pumpName;

    private Long terminalId;

    private String terminalSerialNumber;

    private Long attendantId;

    private String attendantName;

    private LocalDate businessDate;

    private Shift shift;

    private BigDecimal openingMeterReading;

    private BigDecimal closingMeterReading;

    private BigDecimal totalLitresSold;

    private BigDecimal totalRevenue;

    private BigDecimal currentSellingPrice;

}
