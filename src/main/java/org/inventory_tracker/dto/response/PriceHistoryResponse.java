package org.inventory_tracker.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PriceHistoryResponse {

    private Long id;

    private Long stationInventoryId;

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private String reason;

    private String changedBy;

    private LocalDateTime effectiveDate;
}
