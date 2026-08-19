package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.DashboardResponse;
import org.inventory_tracker.dto.response.report.AttendantReportResponse;
import org.inventory_tracker.dto.response.report.DeliveryReportResponse;
import org.inventory_tracker.dto.response.report.ExecutiveSummaryResponse;
import org.inventory_tracker.dto.response.report.InventoryReportResponse;
import org.inventory_tracker.dto.response.report.InventoryTransactionReportResponse;
import org.inventory_tracker.dto.response.report.PaymentReportResponse;
import org.inventory_tracker.dto.response.report.PriceHistoryReportResponse;
import org.inventory_tracker.dto.response.report.ProductReportResponse;
import org.inventory_tracker.dto.response.report.PumpAssignmentReportResponse;
import org.inventory_tracker.dto.response.report.PumpAuditReportResponse;
import org.inventory_tracker.dto.response.report.PumpReportResponse;
import org.inventory_tracker.dto.response.report.SalesReportResponse;
import org.inventory_tracker.dto.response.report.StationReportResponse;
import org.inventory_tracker.service.ReportingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiSuccessResponse<DashboardResponse>>getDashboard() {
        DashboardResponse response = reportingService.getDashboard();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Dashboard retrieved successfully.",
                        response
                )
        );
    }

//     @GetMapping("/executive")
//     public ResponseEntity<ApiSuccessResponse<ExecutiveSummaryResponse>>getExecutiveSummary() {
//         ExecutiveSummaryResponse response = reportingService.getExecutiveSummary();

//         return ResponseEntity.ok(
//                 new ApiSuccessResponse<>(
//                         LocalDateTime.now(),
//                         HttpStatus.OK.value(),
//                         "Executive summary retrieved successfully.",
//                         response
//                 )
//         );
//     }

    @GetMapping("/station")
    public ResponseEntity<ApiSuccessResponse<List<StationReportResponse>>>getStationReport() {
        List<StationReportResponse> response = reportingService.getStationReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiSuccessResponse<List<InventoryReportResponse>>>getInventoryReport() {
        List<InventoryReportResponse> response = reportingService.getInventoryReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/product")
    public ResponseEntity<ApiSuccessResponse<List<ProductReportResponse>>>getProductReport() {
        List<ProductReportResponse> response = reportingService.getProductReport();
        int count = response.size();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/pump")
    public ResponseEntity<ApiSuccessResponse<List<PumpReportResponse>>>getPumpReport() {
        List<PumpReportResponse> response = reportingService.getPumpReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/attendant")
    public ResponseEntity<ApiSuccessResponse<List<AttendantReportResponse>>>getAttendantReport() {
        List<AttendantReportResponse> response =
                reportingService.getAttendantReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/pump-assignment")
    public ResponseEntity<ApiSuccessResponse<List<PumpAssignmentReportResponse>>> getPumpAssignmentReport() {
        List<PumpAssignmentReportResponse> response = reportingService.getPumpAssignmentReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump assignment report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/pump-audit")
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditReportResponse>>> getPumpAuditReport() {
        List<PumpAuditReportResponse> response = reportingService.getPumpAuditReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump audit report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiSuccessResponse<List<SalesReportResponse>>> getSalesReport() {
        List<SalesReportResponse> response = reportingService.getSalesReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Sales report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/delivery")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryReportResponse>>> getDeliveryReport() {
        List<DeliveryReportResponse> response = reportingService.getDeliveryReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Delivery report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/payment")
    public ResponseEntity<ApiSuccessResponse<List<PaymentReportResponse>>> getPaymentReport() {
        List<PaymentReportResponse> response = reportingService.getPaymentReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Payment report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/inventory-transaction")
    public ResponseEntity<ApiSuccessResponse<List<InventoryTransactionReportResponse>>> getInventoryTransactionReport() {
        List<InventoryTransactionReportResponse> response = reportingService.getInventoryTransactionReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory transaction report retrieved successfully.",
                        count,
                        response
                )
        );
    }

    @GetMapping("/price-history")
    public ResponseEntity<ApiSuccessResponse<List<PriceHistoryReportResponse>>> getPriceHistoryReport() {
        List<PriceHistoryReportResponse> response = reportingService.getPriceHistoryReport();
        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Price history report retrieved successfully.",
                        count,
                        response
                )
        );
    }

}
