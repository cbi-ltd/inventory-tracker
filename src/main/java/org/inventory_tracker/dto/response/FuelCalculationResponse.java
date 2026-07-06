package org.inventory_tracker.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FuelCalculationResponse {

    private Double litres;
    private Double amount;
    private Double pricePerUnit;
    private String productName;
}
