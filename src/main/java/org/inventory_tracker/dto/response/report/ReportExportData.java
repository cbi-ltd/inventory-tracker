package org.inventory_tracker.dto.response.report;

import lombok.Getter;
import lombok.Builder;
import java.util.List;

@Getter
@Builder
public class ReportExportData {

    private List<AttendantReportResponse> attendants;

    private List<DeliveryReportResponse> deliveries;

    private List<InventoryReportResponse> inventory;

    private List<InventoryTransactionReportResponse>
            inventoryTransactions;

    private List<PaymentReportResponse> payments;

    private List<PriceHistoryReportResponse> priceHistory;

    private List<ProductReportResponse> products;

    private List<PumpAssignmentReportResponse>
            pumpAssignments;

    private List<PumpAuditReportResponse> pumpAudits;

    private List<PaymentDistributionResponse> paymentDistribution;

    private List<PumpPerformanceResponse> pumpPerformance;

    private List<PumpReportResponse> pumps;

    private List<StationReportResponse> stations;
}