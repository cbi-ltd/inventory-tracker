package org.inventory_tracker.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.enums.ProductType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationInventoryRequest {

    private ProductType productType;

    private Double quantity;

    private Long stationId;
}