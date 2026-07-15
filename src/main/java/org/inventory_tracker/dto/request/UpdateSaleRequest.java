package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateSaleRequest {

    private BigDecimal discountAmount;

    private String remarks;
}
