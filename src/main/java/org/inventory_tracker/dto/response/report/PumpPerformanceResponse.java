package org.inventory_tracker.dto.response.report;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PumpPerformanceResponse {

    private Long pumpId;
    private String pumpNumber;
    private BigDecimal liters;
    private BigDecimal revenue;
}
