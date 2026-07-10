package org.inventory_tracker.dto.response;

import lombok.*;
import org.inventory_tracker.enums.ProductType;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationInventoryResponse {

    private Long id;

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private ProductType productType;

    private String unit;

    private BigDecimal unitPrice;

    private BigDecimal currentQuantity;

    private BigDecimal sellingPrice;

    private BigDecimal reorderLevel;

    private Boolean active;
}