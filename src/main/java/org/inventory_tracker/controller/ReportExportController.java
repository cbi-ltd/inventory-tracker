package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
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
import org.inventory_tracker.dto.response.report.PaymentDistributionResponse;
import org.inventory_tracker.dto.response.report.PriceHistoryReportResponse;
import org.inventory_tracker.dto.response.report.ReportExportData;
import org.inventory_tracker.dto.response.report.StationReportResponse;
import org.inventory_tracker.service.ExcelReportService;
import org.inventory_tracker.service.PdfReportService;
import org.inventory_tracker.service.ReportingService;
import org.inventory_tracker.util.EmailService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportingService reportingService;
    private final ExcelReportService excelReportService;
    private final PdfReportService pdfReportService;
    private final EmailService emailService;

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

    @GetMapping("/payment-distribution/export")
    public ResponseEntity<byte[]> exportPaymentDistributionReport(@RequestParam(required = false) LocalDate businessDate) {
        List<PaymentDistributionResponse> reports = reportingService.getPaymentDistribution(businessDate);
        byte[] excel = excelReportService.exportPaymentDistributionReport(reports);

        return buildExcelResponse(excel, "payment-distribution-report.xlsx");
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


    @GetMapping("/attendants/export/pdf")
    public ResponseEntity<byte[]> exportAttendantReportPdf(@RequestParam(required = false) LocalDate businessDate) {
        List<AttendantReportResponse> reports = reportingService.getAttendantReport(businessDate);
        byte[] pdf = pdfReportService.exportAttendantReport(reports);

        return buildPdfResponse(pdf, "attendant-report.pdf");
    }

    @GetMapping("/deliveries/export/pdf") public ResponseEntity<byte[]> exportDeliveryReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportDeliveryReport( reportingService.getDeliveryReport(businessDate) ), "delivery-report.pdf" );
     }

    @GetMapping("/inventory/export/pdf") public ResponseEntity<byte[]> exportInventoryReportPdf() { 
        return buildPdfResponse( pdfReportService.exportInventoryReport( reportingService.getInventoryReport() ), "inventory-report.pdf" ); 
    }

    @GetMapping("/inventory-transactions/export/pdf") public ResponseEntity<byte[]> exportInventoryTransactionReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportInventoryTransactionReport( reportingService.getInventoryTransactionReport(businessDate) ), "inventory-transaction-report.pdf" ); 
    }

    @GetMapping("/payments/export/pdf") public ResponseEntity<byte[]> exportPaymentReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPaymentReport( reportingService.getPaymentReport(businessDate) ), "payment-report.pdf" ); 
    }

    @GetMapping("/payments/distribution/export/pdf") public ResponseEntity<byte[]> exportPaymentDistributionReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPaymentDistributionReport( reportingService.getPaymentDistribution(businessDate) ), "payment-distribution-report.pdf" ); 
    }

    @GetMapping("/price-history/export/pdf") public ResponseEntity<byte[]> exportPriceHistoryReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPriceHistoryReport( reportingService.getPriceHistoryReport(businessDate) ), "price-history-report.pdf" ); 
    }

    @GetMapping("/products/export/pdf") public ResponseEntity<byte[]> exportProductReportPdf() { 
        return buildPdfResponse( pdfReportService.exportProductReport( reportingService.getProductReport() ), "product-report.pdf" ); 
    }

    @GetMapping("/pump-assignments/export/pdf") public ResponseEntity<byte[]> exportPumpAssignmentReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPumpAssignmentReport( reportingService.getPumpAssignmentReport(businessDate) ), "pump-assignment-report.pdf" ); 
    }

    @GetMapping("/pump-audits/export/pdf") public ResponseEntity<byte[]> exportPumpAuditReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPumpAuditReport( reportingService.getPumpAuditReport(businessDate) ), "pump-audit-report.pdf" ); 
    }

    @GetMapping("/pump-performance/export/pdf") public ResponseEntity<byte[]> exportPumpPerformanceReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPumpPerformanceReport( reportingService.getPumpPerformance(businessDate) ), "pump-performance-report.pdf" ); 
    }

    @GetMapping("/stations/export/pdf") public ResponseEntity<byte[]> exportStationReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportStationReport( reportingService.getStationReport(businessDate) ), "station-report.pdf" ); 
    }

    @GetMapping("/pumps/export/pdf") public ResponseEntity<byte[]> exportPumpReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportPumpReport( reportingService.getPumpReport(businessDate) ), "pump-report.pdf" ); 
    }

    @GetMapping("/sales/export/pdf") public ResponseEntity<byte[]> exportSalesReportPdf( @RequestParam(required = false) LocalDate businessDate) { 
        return buildPdfResponse( pdfReportService.exportSalesReport( reportingService.getSalesReport(businessDate) ), "sales-report.pdf" ); 
    }

    @PostMapping("/attendants/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailAttendantReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        List<AttendantReportResponse> reports = reportingService.getAttendantReport(businessDate);
        byte[] pdf = pdfReportService.exportAttendantReport(reports);

        emailService.sendReport(
                recipient,
                "Attendant Report",
                "Please find the requested attendant report attached.",
                pdf,
                "attendant-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Attendant report emailed successfully", null));
    }

    @PostMapping("/deliveries/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailDeliveryReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportDeliveryReport(reportingService.getDeliveryReport(businessDate));

        emailService.sendReport(
                recipient,
                "Delivery Report",
                "Please find the requested delivery report attached.",
                pdf,
                "delivery-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Delivery report emailed successfully", null));
    }

    @PostMapping("/inventory/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailInventoryReport(@RequestParam String recipient) {
        byte[] pdf = pdfReportService.exportInventoryReport(reportingService.getInventoryReport());

        emailService.sendReport(
                recipient,
                "Inventory Report",
                "Please find the requested inventory report attached.",
                pdf,
                "inventory-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Inventory report emailed successfully", null));
    }

    @PostMapping("/inventory-transactions/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailInventoryTransactionsReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportInventoryTransactionReport(reportingService.getInventoryTransactionReport(businessDate));

        emailService.sendReport(
                recipient,
                "Inventory Transactions Report",
                "Please find the requested inventory transactions report attached.",
                pdf,
                "inventory-transactions-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Inventory transactions report emailed successfully", null));
    }

    @PostMapping("/payments/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPaymentsReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPaymentReport(reportingService.getPaymentReport(businessDate));

        emailService.sendReport(
                recipient,
                "Payments Report",
                "Please find the requested payments report attached.",
                pdf,
                "payments-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Payments report emailed successfully", null));
    }

    @PostMapping("/payments/distribution/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPaymentsDistributionReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPaymentDistributionReport(reportingService.getPaymentDistribution(businessDate));

        emailService.sendReport(
                recipient,
                "Payments Distribution Report",
                "Please find the requested payments distribution report attached.",
                pdf,
                "payments-distribution-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Payments distribution report emailed successfully", null));
    }

    @PostMapping("/price-history/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPriceHistoryReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPriceHistoryReport(reportingService.getPriceHistoryReport(businessDate));

        emailService.sendReport(
                recipient,
                "Price History Report",
                "Please find the requested price history report attached.",
                pdf,
                "price-history-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Price history report emailed successfully", null));
    }

    @PostMapping("/products/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailProductsReport(@RequestParam String recipient) {
        byte[] pdf = pdfReportService.exportProductReport(reportingService.getProductReport());

        emailService.sendReport(
                recipient,
                "Products Report",
                "Please find the requested products report attached.",
                pdf,
                "products-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Products report emailed successfully", null));
    }

    @PostMapping("/pump-assignments/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPumpAssignmentsReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPumpAssignmentReport(reportingService.getPumpAssignmentReport(businessDate));

        emailService.sendReport(
                recipient,
                "Pump Assignments Report",
                "Please find the requested pump assignments report attached.",
                pdf,
                "pump-assignments-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Pump assignments report emailed successfully", null));
    }

    @PostMapping("/pump-audits/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPumpAuditsReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPumpAuditReport(reportingService.getPumpAuditReport(businessDate));

        emailService.sendReport(
                recipient,
                "Pump Audits Report",
                "Please find the requested pump audits report attached.",
                pdf,
                "pump-audits-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Pump audits report emailed successfully", null));
    }

    @PostMapping("/pump-performance/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPumpPerformanceReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPumpPerformanceReport(reportingService.getPumpPerformance(businessDate));

        emailService.sendReport(
                recipient,
                "Pump Performance Report",
                "Please find the requested pump performance report attached.",
                pdf,
                "pump-performance-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Pump performance report emailed successfully", null));
    }

    @PostMapping("/pumps/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailPumpReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportPumpReport(reportingService.getPumpReport(businessDate));

        emailService.sendReport(
                recipient,
                "Pump Report",
                "Please find the requested pump report attached.",
                pdf,
                "pump-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Pump report emailed successfully", null));
    }

    @PostMapping("/stations/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailStationReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportStationReport(reportingService.getStationReport(businessDate));

        emailService.sendReport(
                recipient,
                "Station Report",
                "Please find the requested station report attached.",
                pdf,
                "station-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Station report emailed successfully", null));
    }

    @PostMapping("/sales/email")
    public ResponseEntity<ApiSuccessResponse<Void>> emailSalesReport(@RequestParam String recipient, @RequestParam(required = false) LocalDate businessDate) {
        byte[] pdf = pdfReportService.exportSalesReport(reportingService.getSalesReport(businessDate));

        emailService.sendReport(
                recipient,
                "Sales Report",
                "Please find the requested sales report attached.",
                pdf,
                "sales-report.pdf"
        );

        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Sales report emailed successfully", null));
    }

    









    private ResponseEntity<byte[]> buildPdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
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
