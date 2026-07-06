package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuelCalculationRequest {

    private Long businessTypeId;
    private Double litres;
    private Double amount;
}
