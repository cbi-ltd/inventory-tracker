package org.inventory_tracker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCountResponse {

    private Long id;

    private String countNumber;

    private LocalDate businessDate;

    private Long stationInventoryId;

    private Long stationId;
    private String stationName;

    private Long productId;
    private String productName;

    private BigDecimal systemQuantity;

    private BigDecimal countedQuantity;

    private BigDecimal variance;

    private String countedBy;

    private String remarks;

    private Long stockAdjustmentId;

    private String stockAdjustmentNumber;
}
