package org.inventory_tracker.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryReportResponse {

    private Long historyId;

    private Long stationId;
    private String stationName;

    private Long productId;
    private String productName;

    private BigDecimal oldSellingPrice;

    private BigDecimal newSellingPrice;

    private BigDecimal priceDifference;

    private String changedBy;

    private LocalDate businessDate;

    private LocalDateTime changedAt;
}
