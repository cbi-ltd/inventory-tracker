package org.inventory_tracker.dto.response;


import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportResponse {

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private BigDecimal currentQuantity;

    private BigDecimal sellingPrice;

    private BigDecimal inventoryValue;

    private BigDecimal reorderLevel;

    private Boolean belowReorderLevel;
}
