package org.inventory_tracker.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveSummaryResponse {

    private LocalDate businessDate;

    private Long totalStations;

    private Long totalProducts;

    private Long totalPumps;

    private Long totalAttendants;

    private BigDecimal totalInventoryQuantity;

    private BigDecimal totalInventoryValue;

    private Long deliveriesToday;

    private Long transfersToday;

    private Long adjustmentsToday;

    private Long stockCountsToday;

    private Long lowStockProducts;

    private Long lowStockStations;
}
