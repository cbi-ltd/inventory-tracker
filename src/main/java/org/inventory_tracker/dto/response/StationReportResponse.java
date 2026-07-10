package org.inventory_tracker.dto.response;


import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationReportResponse {

    private Long stationId;

    private String stationName;

    private Long totalProducts;

    private Long totalPumps;

    private Long activePumpAssignments;

    private BigDecimal inventoryQuantity;

    private BigDecimal inventoryValue;

    private Long deliveries;

    private Long transfersIn;

    private Long transfersOut;

    private Long adjustments;

    private Long stockCounts;

    private Long lowStockProducts;
}
