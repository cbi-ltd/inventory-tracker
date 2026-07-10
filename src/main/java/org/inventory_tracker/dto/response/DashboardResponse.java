package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import lombok.*;
import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long totalStations;

    private Long totalProducts;

    private Long totalPumps;

    private Long totalAttendants;

    private Long activePumpAssignments;

    private Long totalDeliveriesToday;

    private Long totalTransfersToday;

    private Long totalAdjustmentsToday;

    private Long totalStockCountsToday;

    private BigDecimal totalInventoryQuantity;

    private BigDecimal totalInventoryValue;

    private Long lowStockProducts;

    private Long lowStockStations;

    private LocalDate businessDate;
}
