package org.inventory_tracker.dto.response;

import lombok.*;
import org.inventory_tracker.enums.ProductType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationInventoryResponse {

    private Long id;

    private ProductType productType;

    private Double quantity;

    private Long stationId;

    private String stationName;
}