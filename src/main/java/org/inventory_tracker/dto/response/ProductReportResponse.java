package org.inventory_tracker.dto.response;


import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportResponse {

    private Long productId;

    private String productName;

    private String productCode;

    private Long stationsStockingProduct;

    private BigDecimal totalQuantity;

    private BigDecimal averageSellingPrice;

    private BigDecimal inventoryValue;

    private Long deliveries;

    private Long transfers;

    private Long adjustments;

    private Long stockCounts;
}
