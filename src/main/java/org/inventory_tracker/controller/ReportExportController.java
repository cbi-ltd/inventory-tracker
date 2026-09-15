package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.response.report.AttendantReportResponse;
import org.inventory_tracker.dto.response.report.DeliveryReportResponse;
import org.inventory_tracker.dto.response.report.InventoryReportResponse;
import org.inventory_tracker.dto.response.report.InventoryTransactionReportResponse;
import org.inventory_tracker.dto.response.report.PaymentReportResponse;
import org.inventory_tracker.dto.response.report.ProductReportResponse;
import org.inventory_tracker.dto.response.report.PumpReportResponse;
import org.inventory_tracker.dto.response.report.PumpAuditReportResponse;
import org.inventory_tracker.dto.response.report.PumpPerformanceResponse;
import org.inventory_tracker.dto.response.report.PumpAssignmentReportResponse;
import org.inventory_tracker.dto.response.report.PriceHistoryReportResponse;
import org.inventory_tracker.dto.response.report.ReportExportData;
import org.inventory_tracker.dto.response.report.StationReportResponse;
import org.inventory_tracker.service.ExcelReportService;
import org.inventory_tracker.service.ReportingService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportingService reportingService;
    private final ExcelReportService excelReportService;

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportAllReports(@RequestParam(required = false) LocalDate businessDate) {
        ReportExportData data = ReportExportData.builder()
                .attendants(reportingService.getAttendantReport(businessDate))
                .deliveries(reportingService.getDeliveryReport(businessDate))
                .inventory(reportingService.getInventoryReport())
                .inventoryTransactions(reportingService.getInventoryTransactionReport(businessDate))
                .payments(reportingService.getPaymentReport(businessDate))
                .priceHistory(reportingService.getPriceHistoryReport(businessDate))
                .products(reportingService.getProductReport())
                .pumpAssignments(reportingService.getPumpAssignmentReport(businessDate))
                .pumpAudits(reportingService.getPumpAuditReport(businessDate))
                .pumpPerformance(reportingService.getPumpPerformance(businessDate))
                .paymentDistribution(reportingService.getPaymentDistribution(businessDate))
                .pumps(reportingService.getPumpReport(businessDate))
                .stations(reportingService.getStationReport(businessDate))
                .build();

        byte[] excel = excelReportService.generateReportWorkbook(data);
        String filename = "fuel-flow-reports-" + (businessDate != null ? businessDate : LocalDate.now()) + ".xlsx";

        return buildExcelResponse(excel, filename);
    }


    @GetMapping("/attendants/export")
    public ResponseEntity<byte[]> exportAttendantReport(@RequestParam(required = false) LocalDate businessDate) {
        List<AttendantReportResponse> reports = reportingService.getAttendantReport(businessDate);
        byte[] excel = excelReportService.exportAttendantReport(reports);

        return buildExcelResponse(excel, "attendant-report.xlsx");
    }


    @GetMapping("/deliveries/export")
    public ResponseEntity<byte[]> exportDeliveryReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<DeliveryReportResponse> reports =
                reportingService.getDeliveryReport(businessDate);

        byte[] excel =
                excelReportService.exportDeliveryReport(reports);

        return buildExcelResponse(
                excel,
                "delivery-report.xlsx"
        );
    }


    @GetMapping("/inventory/export")
    public ResponseEntity<byte[]> exportInventoryReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<InventoryReportResponse> reports =
                reportingService.getInventoryReport();

        byte[] excel =
                excelReportService.exportInventoryReport(reports);

        return buildExcelResponse(
                excel,
                "inventory-report.xlsx"
        );
    }

    @GetMapping("/inventory-transactions/export")
    public ResponseEntity<byte[]> exportInventoryTransactionReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<InventoryTransactionReportResponse> reports =
                reportingService.getInventoryTransactionReport(businessDate);

        byte[] excel =
                excelReportService.exportInventoryTransactionReport(reports);

        return buildExcelResponse(
                excel,
                "inventory-transaction-report.xlsx"
        );
    }


    @GetMapping("/payments/export")
    public ResponseEntity<byte[]> exportPaymentReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PaymentReportResponse> reports =
                reportingService.getPaymentReport(businessDate);

        byte[] excel =
                excelReportService.exportPaymentReport(reports);

        return buildExcelResponse(
                excel,
                "payment-report.xlsx"
        );
    }

    @GetMapping("/price-history/export")
    public ResponseEntity<byte[]> exportPriceHistoryReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PriceHistoryReportResponse> reports =
                reportingService.getPriceHistoryReport(businessDate);

        byte[] excel =
                excelReportService.exportPriceHistoryReport(reports);

        return buildExcelResponse(
                excel,
                "price-history-report.xlsx"
        );
    }


    @GetMapping("/products/export")
    public ResponseEntity<byte[]> exportProductReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<ProductReportResponse> reports =
                reportingService.getProductReport();

        byte[] excel =
                excelReportService.exportProductReport(reports);

        return buildExcelResponse(
                excel,
                "product-report.xlsx"
        );
    }

     @GetMapping("/pump-assignments/export")
    public ResponseEntity<byte[]> exportPumpAssignmentReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PumpAssignmentReportResponse> reports =
                reportingService.getPumpAssignmentReport(businessDate);

        byte[] excel =
                excelReportService.exportPumpAssignmentReport(reports);

        return buildExcelResponse(
                excel,
                "pump-assignment-report.xlsx"
        );
    }


    @GetMapping("/pump-audits/export")
    public ResponseEntity<byte[]> exportPumpAuditReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PumpAuditReportResponse> reports =
                reportingService.getPumpAuditReport(businessDate);

        byte[] excel =
                excelReportService.exportPumpAuditReport(reports);

        return buildExcelResponse(
                excel,
                "pump-audit-report.xlsx"
        );
    }

    @GetMapping("/pump-performance/export")
    public ResponseEntity<byte[]> exportPumpPerformanceReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PumpPerformanceResponse> reports =
                reportingService.getPumpPerformance(businessDate);

        byte[] excel =
                excelReportService.exportPumpPerformanceReport(reports);

        return buildExcelResponse(
                excel,
                "pump-performance-report.xlsx"
        );
    }


    @GetMapping("/pumps/export")
    public ResponseEntity<byte[]> exportPumpReport(
            @RequestParam(required = false) LocalDate businessDate) {

        List<PumpReportResponse> reports =
                reportingService.getPumpReport(businessDate);

        byte[] excel =
                excelReportService.exportPumpReport(reports);

        return buildExcelResponse(
                excel,
                "pump-report.xlsx"
        );
    }

    @GetMapping("/stations/export")
    public ResponseEntity<byte[]> exportStationReport(@RequestParam(required = false) LocalDate businessDate) {
        List<StationReportResponse> reports = reportingService.getStationReport(businessDate);
        byte[] excel = excelReportService.exportStationReport(reports);

        return buildExcelResponse(excel, "station-report.xlsx");
    }





    private ResponseEntity<byte[]> buildExcelResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType( MediaType.parseMediaType( "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ) );
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(content.length);

        return ResponseEntity.ok().headers(headers).body(content);
    }
}
