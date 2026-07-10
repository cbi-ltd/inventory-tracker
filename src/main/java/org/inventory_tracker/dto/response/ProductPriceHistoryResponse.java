package org.inventory_tracker.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.inventory_tracker.enums.ProductType;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceHistoryResponse {

    private Long id;

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private ProductType productType;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private String reason;

    private String changedBy;

    private LocalDate businessDate;

    private LocalDateTime changedAt;
}
