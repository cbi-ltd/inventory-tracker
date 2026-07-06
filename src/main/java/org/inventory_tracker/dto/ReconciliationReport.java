package org.inventory_tracker.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReconciliationReport {

    private String merchantId;

    private String outletId;

    private Long pumpAuditId;

    private Double openingReading;

    private Double closingReading;

    private Double actualDispensedLitres;

    private Double recordedSalesLitres;

    private Double volumeVariance;

    private Double expectedRevenue;

    private Double actualRevenue;

    private Double revenueVariance;

    private Boolean volumeVarianceDetected;

    private Boolean revenueVarianceDetected;
}
