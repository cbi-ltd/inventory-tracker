package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter
public class ProductPriceHistoryFilterRequest {

    private Long id;

    private Long stationId;

    private Long productId;

    private LocalDate businessDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String changedBy;
}
