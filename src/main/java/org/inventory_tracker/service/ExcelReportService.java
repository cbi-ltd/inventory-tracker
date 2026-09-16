package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.inventory_tracker.dto.response.report.ReportExportData;
import org.inventory_tracker.dto.response.report.AttendantReportResponse;
import org.inventory_tracker.dto.response.report.DeliveryReportResponse;
import org.inventory_tracker.dto.response.report.InventoryReportResponse;
import org.inventory_tracker.dto.response.report.InventoryTransactionReportResponse;
import org.inventory_tracker.dto.response.report.PaymentDistributionResponse;
import org.inventory_tracker.dto.response.report.PaymentReportResponse;
import org.inventory_tracker.dto.response.report.PriceHistoryReportResponse;
import org.inventory_tracker.dto.response.report.ProductReportResponse;
import org.inventory_tracker.dto.response.report.PumpAssignmentReportResponse;
import org.inventory_tracker.dto.response.report.PumpAuditReportResponse;
import org.inventory_tracker.dto.response.report.PumpPerformanceResponse;
import org.inventory_tracker.dto.response.report.PumpReportResponse;
import org.inventory_tracker.dto.response.report.StationReportResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private static final String REPORT_TITLE = "Fuel Flow Reports";
    private static final String DATE_FORMAT = "yyyy-mm-dd";
    private static final String DATETIME_FORMAT = "yyyy-mm-dd hh:mm:ss";

    public byte[] generateReportWorkbook(ReportExportData data) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            createAttendantsSheet(workbook, data.getAttendants());
            createDeliveriesSheet(workbook, data.getDeliveries());
            createInventorySheet(workbook, data.getInventory());
            createInventoryTransactionsSheet(workbook, data.getInventoryTransactions());
            createPaymentsSheet(workbook, data.getPayments());
            createPriceHistorySheet(workbook, data.getPriceHistory());
            createProductsSheet(workbook, data.getProducts());
            createPumpAssignmentsSheet(workbook, data.getPumpAssignments());
            createPumpAuditsSheet(workbook, data.getPumpAudits());
            createPumpPerformanceSheet(workbook, data.getPumpPerformance());
            createPaymentDistributionSheet(workbook, data.getPaymentDistribution());
            createPumpsSheet(workbook, data.getPumps());
            createStationsSheet(workbook, data.getStations());

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate Excel report", e);
        }
    }


    private void createAttendantsSheet(
            Workbook workbook,
            List<AttendantReportResponse> reports) {

        String[] headers = {
                "Attendant ID",
                "Username",
                "Full Name",
                "Station",
                "Assigned Pump",
                "Active",
                "Completed Shifts",
                "Stock Counts Performed",
                "Adjustments Performed",
                "Deliveries Received",
                "Transfers Initiated"
        };

        Sheet sheet = createSheet(
                workbook,
                "Attendants",
                headers
        );

        int rowIndex = 1;

        for (AttendantReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getAttendantId());
            writeString(row, 1, report.getUsername());
            writeString(row, 2, report.getFullName());
            writeString(row, 3, report.getStationName());
            writeString(row, 4, report.getAssignedPump());
            writeBoolean(row, 5, report.getActive());
            writeLong(row, 6, report.getCompletedShifts());
            writeLong(row, 7, report.getStockCountsPerformed());
            writeLong(row, 8, report.getAdjustmentsPerformed());
            writeLong(row, 9, report.getDeliveriesReceived());
            writeLong(row, 10, report.getTransfersInitiated());
        }

        finishSheet(sheet, headers.length);
    }


    private void createDeliveriesSheet(
            Workbook workbook,
            List<DeliveryReportResponse> reports) {

        String[] headers = {
                "Delivery ID",
                "Delivery Number",
                "Station ID",
                "Station",
                "Product ID",
                "Product",
                "Station Inventory ID",
                "Quantity Delivered",
                "Cost Per Unit",
                "Total Cost",
                "Status",
                "Business Date",
                "Received At",
                "Reversed At",
                "Remarks",
                "Reversal Reason"
        };

        Sheet sheet = createSheet(
                workbook,
                "Deliveries",
                headers
        );

        int rowIndex = 1;

        for (DeliveryReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getDeliveryId());
            writeString(row, 1, report.getDeliveryNumber());
            writeLong(row, 2, report.getStationId());
            writeString(row, 3, report.getStationName());
            writeLong(row, 4, report.getProductId());
            writeString(row, 5, report.getProductName());
            writeLong(row, 6, report.getStationInventoryId());
            writeDecimal(row, 7, report.getQuantityDelivered());
            writeDecimal(row, 8, report.getCostPerUnit());
            writeDecimal(row, 9, report.getTotalCost());

            writeEnum(row, 10, report.getStatus());

            writeDate(row, 11, report.getBusinessDate());
            writeDateTime(row, 12, report.getReceivedAt());
            writeDateTime(row, 13, report.getReversedAt());

            writeString(row, 14, report.getRemarks());
            writeString(row, 15, report.getReversalReason());
        }

        finishSheet(sheet, headers.length);
    }


    private void createInventorySheet(
            Workbook workbook,
            List<InventoryReportResponse> reports) {

        String[] headers = {
                "Station ID",
                "Station",
                "Product ID",
                "Product",
                "Current Quantity",
                "Selling Price",
                "Inventory Value",
                "Reorder Level",
                "Below Reorder Level"
        };

        Sheet sheet = createSheet(
                workbook,
                "Inventory",
                headers
        );

        int rowIndex = 1;

        for (InventoryReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getStationId());
            writeString(row, 1, report.getStationName());
            writeLong(row, 2, report.getProductId());
            writeString(row, 3, report.getProductName());
            writeDecimal(row, 4, report.getCurrentQuantity());
            writeDecimal(row, 5, report.getSellingPrice());
            writeDecimal(row, 6, report.getInventoryValue());
            writeDecimal(row, 7, report.getReorderLevel());
            writeBoolean(row, 8, report.getBelowReorderLevel());
        }

        finishSheet(sheet, headers.length);
    }


    private void createInventoryTransactionsSheet(
            Workbook workbook,
            List<InventoryTransactionReportResponse> reports) {

        String[] headers = {
                "Transaction ID",
                "Station ID",
                "Station",
                "Station Inventory ID",
                "Product ID",
                "Product",
                "Transaction Type",
                "Quantity",
                "Balance Before",
                "Balance After",
                "Remarks",
                "Reference Number",
                "Business Date",
                "Transaction Time"
        };

        Sheet sheet = createSheet(
                workbook,
                "Inventory Transactions",
                headers
        );

        int rowIndex = 1;

        for (InventoryTransactionReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getTransactionId());
            writeLong(row, 1, report.getStationId());
            writeString(row, 2, report.getStationName());
            writeLong(row, 3, report.getStationInventoryId());
            writeLong(row, 4, report.getProductId());
            writeString(row, 5, report.getProductName());

            writeEnum(row, 6, report.getTransactionType());

            writeDecimal(row, 7, report.getQuantity());
            writeDecimal(row, 8, report.getBalanceBeforeTransaction());
            writeDecimal(row, 9, report.getBalanceAfterTransaction());

            writeString(row, 10, report.getRemarks());
            writeString(row, 11, report.getReferenceNumber());

            writeDate(row, 12, report.getBusinessDate());
            writeDateTime(row, 13, report.getTransactionTime());
        }

        finishSheet(sheet, headers.length);
    }


    private void createPaymentsSheet(
            Workbook workbook,
            List<PaymentReportResponse> reports) {

        String[] headers = {
                "Payment ID",
                "Sale ID",
                "Sale Number",
                "Transaction Reference",
                "Station ID",
                "Station",
                "Pump ID",
                "Pump Number",
                "Attendant ID",
                "Attendant",
                "Amount",
                "Payment Method",
                "Payment Status",
                "Business Date",
                "Payment Time"
        };

        Sheet sheet = createSheet(
                workbook,
                "Payments",
                headers
        );

        int rowIndex = 1;

        for (PaymentReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getPaymentId());
            writeLong(row, 1, report.getSaleId());
            writeString(row, 2, report.getSaleNumber());
            writeString(row, 3, report.getTransactionReference());
            writeLong(row, 4, report.getStationId());
            writeString(row, 5, report.getStationName());
            writeLong(row, 6, report.getPumpId());
            writeString(row, 7, report.getPumpNumber());
            writeLong(row, 8, report.getAttendantId());
            writeString(row, 9, report.getAttendantName());

            writeDecimal(row, 10, report.getAmount());

            writeEnum(row, 11, report.getPaymentMethod());
            writeEnum(row, 12, report.getPaymentStatus());

            writeDate(row, 13, report.getBusinessDate());
            writeDateTime(row, 14, report.getPaymentTime());
        }

        finishSheet(sheet, headers.length);
    }


    private void createPriceHistorySheet(
            Workbook workbook,
            List<PriceHistoryReportResponse> reports) {

        String[] headers = {
                "History ID",
                "Station ID",
                "Station",
                "Product ID",
                "Product",
                "Old Selling Price",
                "New Selling Price",
                "Price Difference",
                "Changed By",
                "Business Date",
                "Changed At"
        };

        Sheet sheet = createSheet(
                workbook,
                "Price History",
                headers
        );

        int rowIndex = 1;

        for (PriceHistoryReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getHistoryId());
            writeLong(row, 1, report.getStationId());
            writeString(row, 2, report.getStationName());
            writeLong(row, 3, report.getProductId());
            writeString(row, 4, report.getProductName());

            writeDecimal(row, 5, report.getOldSellingPrice());
            writeDecimal(row, 6, report.getNewSellingPrice());
            writeDecimal(row, 7, report.getPriceDifference());

            writeString(row, 8, report.getChangedBy());

            writeDate(row, 9, report.getBusinessDate());
            writeDateTime(row, 10, report.getChangedAt());
        }

        finishSheet(sheet, headers.length);
    }

    private void createProductsSheet(
            Workbook workbook,
            List<ProductReportResponse> reports) {

        String[] headers = {
                "Product ID",
                "Product",
                "Product Code",
                "Stations Stocking Product",
                "Total Quantity",
                "Average Selling Price",
                "Inventory Value",
                "Deliveries",
                "Transfers",
                "Adjustments",
                "Stock Counts"
        };

        Sheet sheet = createSheet(
                workbook,
                "Products",
                headers
        );

        int rowIndex = 1;

        for (ProductReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getProductId());
            writeString(row, 1, report.getProductName());
            writeString(row, 2, report.getProductCode());
            writeLong(row, 3, report.getStationsStockingProduct());
            writeDecimal(row, 4, report.getTotalQuantity());
            writeDecimal(row, 5, report.getAverageSellingPrice());
            writeDecimal(row, 6, report.getInventoryValue());
            writeLong(row, 7, report.getDeliveries());
            writeLong(row, 8, report.getTransfers());
            writeLong(row, 9, report.getAdjustments());
            writeLong(row, 10, report.getStockCounts());
        }

        finishSheet(sheet, headers.length);
    }


    private void createPumpAssignmentsSheet(
            Workbook workbook,
            List<PumpAssignmentReportResponse> reports) {

        String[] headers = {
                "Assignment ID",
                "Station ID",
                "Station",
                "Pump ID",
                "Pump Number",
                "Pump Name",
                "Attendant ID",
                "Attendant",
                "Terminal ID",
                "Terminal Serial Number",
                "Assignment Date",
                "Shift",
                "Active"
        };

        Sheet sheet = createSheet(
                workbook,
                "Pump Assignments",
                headers
        );

        int rowIndex = 1;

        for (PumpAssignmentReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getAssignmentId());
            writeLong(row, 1, report.getStationId());
            writeString(row, 2, report.getStationName());
            writeLong(row, 3, report.getPumpId());
            writeString(row, 4, report.getPumpNumber());
            writeString(row, 5, report.getPumpName());
            writeLong(row, 6, report.getAttendantId());
            writeString(row, 7, report.getAttendantName());
            writeLong(row, 8, report.getTerminalId());
            writeString(row, 9, report.getTerminalSerialNumber());

            writeDate(row, 10, report.getAssignmentDate());
            writeEnum(row, 11, report.getShift());
            writeBoolean(row, 12, report.getActive());
        }

        finishSheet(sheet, headers.length);
    }


    private void createPumpAuditsSheet(
            Workbook workbook,
            List<PumpAuditReportResponse> reports) {

        String[] headers = {
                "Audit ID",
                "Assignment ID",
                "Station ID",
                "Station",
                "Pump ID",
                "Pump Number",
                "Pump Name",
                "Attendant ID",
                "Attendant",
                "Business Date",
                "Shift",
                "Clock In",
                "Clock Out",
                "Opening Reading",
                "Closing Reading",
                "Total Dispensed"
        };

        Sheet sheet = createSheet(
                workbook,
                "Pump Audits",
                headers
        );

        int rowIndex = 1;

        for (PumpAuditReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getAuditId());
            writeLong(row, 1, report.getAssignmentId());
            writeLong(row, 2, report.getStationId());
            writeString(row, 3, report.getStationName());
            writeLong(row, 4, report.getPumpId());
            writeString(row, 5, report.getPumpNumber());
            writeString(row, 6, report.getPumpName());
            writeLong(row, 7, report.getAttendantId());
            writeString(row, 8, report.getAttendantName());

            writeDate(row, 9, report.getBusinessDate());
            writeEnum(row, 10, report.getShift());

            writeDateTime(row, 11, report.getClockInTime());
            writeDateTime(row, 12, report.getClockOutTime());

            writeDecimal(row, 13, report.getOpeningReading());
            writeDecimal(row, 14, report.getClosingReading());
            writeDecimal(row, 15, report.getTotalDispensed());
        }

        finishSheet(sheet, headers.length);
    }

    private void createPumpPerformanceSheet(Workbook workbook, List<PumpPerformanceResponse> reports) {
        String[] headers = {"Pump ID", "Pump Number","Liters", "Revenue"};
        Sheet sheet = createSheet(workbook, "Pump Performance", headers);
        int rowIndex = 1;

        for (PumpPerformanceResponse report : reports) {
            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getPumpId());
            writeString(row, 1, report.getPumpNumber());
            writeDecimal(row, 2, report.getLiters());
            writeDecimal(row, 3, report.getRevenue());
        }

        finishSheet(sheet, headers.length);
    }

//     private void createPaymentDistributionSheet(Workbook workbook, List<PaymentDistributionResponse> reports) {
//         Sheet sheet = workbook.createSheet("Payment Distribution");
//         Row header = sheet.createRow(0);
//         header.createCell(0).setCellValue("Payment Method");
//         header.createCell(1).setCellValue("Amount");
//         int rowNum = 1;

//         for (PaymentDistributionResponse report : reports) {
//                 Row row = sheet.createRow(rowNum++);

//                 row.createCell(0).setCellValue(report.getPaymentMethod() != null ? report.getPaymentMethod().name() : "");
//                 row.createCell(1).setCellValue(report.getAmount() != null ? report.getAmount().doubleValue() : 0.0);
//         }

//         sheet.autoSizeColumn(0);
//         sheet.autoSizeColumn(1);
//     }

    private void createPaymentDistributionSheet(Workbook workbook, List<PaymentDistributionResponse> reports) {
	String[] headers = {"Payment Method", "Amount"};
        Sheet sheet = createSheet(workbook, "Payment Distribution", headers);
	int rowIndex = 1;

        for (PaymentDistributionResponse report : reports) {
            Row row = sheet.createRow(rowIndex++);

            writeString(row, 0, report.getPaymentMethod().name());
            writeDecimal(row, 1, report.getAmount());

        }

        finishSheet(sheet, headers.length);
    }


    private void createPumpsSheet(
            Workbook workbook,
            List<PumpReportResponse> reports) {

        String[] headers = {
                "Pump ID",
                "Pump Number",
                "Pump Name",
                "Station",
                "Product",
                "Active",
                "Total Assignments",
                "Active Assignments",
                "Audits Completed"
        };

        Sheet sheet = createSheet(
                workbook,
                "Pumps",
                headers
        );

        int rowIndex = 1;

        for (PumpReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getPumpId());
            writeString(row, 1, report.getPumpNumber());
            writeString(row, 2, report.getPumpName());
            writeString(row, 3, report.getStationName());
            writeString(row, 4, report.getProductName());
            writeBoolean(row, 5, report.getActive());
            writeLong(row, 6, report.getTotalAssignments());
            writeLong(row, 7, report.getActiveAssignments());
            writeLong(row, 8, report.getAuditsCompleted());
        }

        finishSheet(sheet, headers.length);
    }


    private void createStationsSheet(
            Workbook workbook,
            List<StationReportResponse> reports) {

        String[] headers = {
                "Station ID",
                "Station",
                "Total Products",
                "Total Pumps",
                "Active Pump Assignments",
                "Inventory Quantity",
                "Inventory Value",
                "Deliveries",
                "Transfers In",
                "Transfers Out",
                "Adjustments",
                "Stock Counts",
                "Low Stock Products"
        };

        Sheet sheet = createSheet(
                workbook,
                "Stations",
                headers
        );

        int rowIndex = 1;

        for (StationReportResponse report : reports) {

            Row row = sheet.createRow(rowIndex++);

            writeLong(row, 0, report.getStationId());
            writeString(row, 1, report.getStationName());
            writeLong(row, 2, report.getTotalProducts());
            writeLong(row, 3, report.getTotalPumps());
            writeLong(row, 4, report.getActivePumpAssignments());
            writeDecimal(row, 5, report.getInventoryQuantity());
            writeDecimal(row, 6, report.getInventoryValue());
            writeLong(row, 7, report.getDeliveries());
            writeLong(row, 8, report.getTransfersIn());
            writeLong(row, 9, report.getTransfersOut());
            writeLong(row, 10, report.getAdjustments());
            writeLong(row, 11, report.getStockCounts());
            writeLong(row, 12, report.getLowStockProducts());
        }

        finishSheet(sheet, headers.length);
    }

    private Sheet createSheet(
            Workbook workbook,
            String sheetName,
            String[] headers) {

        Sheet sheet = workbook.createSheet(sheetName);

        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {

            Cell cell = headerRow.createCell(i);

            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(
                new org.apache.poi.ss.util.CellRangeAddress(
                        0,
                        0,
                        0,
                        headers.length - 1
                )
        );

        return sheet;
    }

    private void finishSheet(
            Sheet sheet,
            int numberOfColumns) {

        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);

            int currentWidth = sheet.getColumnWidth(i);

            // Prevent extremely wide columns
            sheet.setColumnWidth(
                    i,
                    Math.min(currentWidth + 1000, 15000)
            );
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);

        style.setFont(font);

        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        style.setBorderBottom(
                BorderStyle.THIN
        );

        return style;
    }


    private void writeString(Row row, int column, String value) {
        if (value != null) {
            row.createCell(column).setCellValue(value);
        }
    }

    private void writeLong(Row row, int column, Long value) {
        if (value != null) {
            row.createCell(column).setCellValue(value);
        }
    }

    private void writeBoolean(Row row,int column, Boolean value) {
        if (value != null) {
            row.createCell(column).setCellValue(value);
        }
    }

    private void writeDecimal(Row row, int column, BigDecimal value) {
        if (value != null) {
            row.createCell(column).setCellValue(value.doubleValue());
        }
    }

    private void writeEnum(Row row, int column, Enum<?> value) {
        if (value != null) {
            row.createCell(column).setCellValue(value.name());
        }
    }

    private void writeDate(Row row, int column, LocalDate value) {
        if (value == null) { return; }
        Cell cell = row.createCell(column);
        cell.setCellValue(java.sql.Date.valueOf(value));
    }

    private void writeDateTime(Row row, int column, LocalDateTime value) {
        if (value == null) { return; }
        Cell cell = row.createCell(column);
        cell.setCellValue(java.sql.Timestamp.valueOf(value));
    }




    public byte[] exportAttendantReport(List<AttendantReportResponse> reports) {

        return createWorkbook("Attendant Report", "Attendants",
                new String[]{
                        "Attendant ID",
                        "Username",
                        "Full Name",
                        "Station",
                        "Assigned Pump",
                        "Active",
                        "Completed Shifts",
                        "Stock Counts Performed",
                        "Adjustments Performed",
                        "Deliveries Received",
                        "Transfers Initiated"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getAttendantId());
                    writeString(row, 1, report.getUsername());
                    writeString(row, 2, report.getFullName());
                    writeString(row, 3, report.getStationName());
                    writeString(row, 4, report.getAssignedPump());
                    writeBoolean(row, 5, report.getActive());
                    writeLong(row, 6, report.getCompletedShifts());
                    writeLong(row, 7, report.getStockCountsPerformed());
                    writeLong(row, 8, report.getAdjustmentsPerformed());
                    writeLong(row, 9, report.getDeliveriesReceived());
                    writeLong(row, 10, report.getTransfersInitiated());
                }
        );
    }

    public byte[] exportDeliveryReport(List<DeliveryReportResponse> reports) {

        return createWorkbook(
                "Delivery Report",
                "Deliveries",
                new String[]{
                        "Delivery ID",
                        "Delivery Number",
                        "Station ID",
                        "Station",
                        "Product ID",
                        "Product",
                        "Station Inventory ID",
                        "Quantity Delivered",
                        "Cost Per Unit",
                        "Total Cost",
                        "Status",
                        "Business Date",
                        "Received At",
                        "Reversed At",
                        "Remarks",
                        "Reversal Reason"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getDeliveryId());
                    writeString(row, 1, report.getDeliveryNumber());
                    writeLong(row, 2, report.getStationId());
                    writeString(row, 3, report.getStationName());
                    writeLong(row, 4, report.getProductId());
                    writeString(row, 5, report.getProductName());
                                        writeLong(row, 6, report.getStationInventoryId());
                    writeDecimal(row, 7, report.getQuantityDelivered());
                    writeDecimal(row, 8, report.getCostPerUnit());
                    writeDecimal(row, 9, report.getTotalCost());
                    writeEnum(row, 10, report.getStatus());
                    writeDate(row, 11, report.getBusinessDate());
                    writeDateTime(row, 12, report.getReceivedAt());
                    writeDateTime(row, 13, report.getReversedAt());
                    writeString(row, 14, report.getRemarks());
                    writeString(row, 15, report.getReversalReason());
                }
        );
    }

    public byte[] exportInventoryReport(
            List<InventoryReportResponse> reports) {

        return createWorkbook(
                "Inventory Report",
                "Inventory",
                new String[]{
                        "Station ID",
                        "Station",
                        "Product ID",
                        "Product",
                        "Current Quantity",
                        "Selling Price",
                        "Inventory Value",
                        "Reorder Level",
                        "Below Reorder Level"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getStationId());
                    writeString(row, 1, report.getStationName());
                    writeLong(row, 2, report.getProductId());
                    writeString(row, 3, report.getProductName());
                    writeDecimal(row, 4, report.getCurrentQuantity());
                    writeDecimal(row, 5, report.getSellingPrice());
                    writeDecimal(row, 6, report.getInventoryValue());
                    writeDecimal(row, 7, report.getReorderLevel());
                    writeBoolean(row, 8, report.getBelowReorderLevel());
                }
        );
    }

    public byte[] exportInventoryTransactionReport(
            List<InventoryTransactionReportResponse> reports) {

        return createWorkbook(
                "Inventory Transaction Report",
                "Inventory Transactions",
                new String[]{
                        "Transaction ID",
                        "Station ID",
                        "Station",
                        "Station Inventory ID",
                        "Product ID",
                        "Product",
                        "Transaction Type",
                        "Quantity",
                        "Balance Before",
                        "Balance After",
                        "Remarks",
                        "Reference Number",
                        "Business Date",
                        "Transaction Time"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getTransactionId());
                    writeLong(row, 1, report.getStationId());
                    writeString(row, 2, report.getStationName());
                    writeLong(row, 3, report.getStationInventoryId());
                    writeLong(row, 4, report.getProductId());
                    writeString(row, 5, report.getProductName());
                    writeEnum(row, 6, report.getTransactionType());
                    writeDecimal(row, 7, report.getQuantity());
                    writeDecimal(row, 8, report.getBalanceBeforeTransaction());
                    writeDecimal(row, 9, report.getBalanceAfterTransaction());
                    writeString(row, 10, report.getRemarks());
                    writeString(row, 11, report.getReferenceNumber());
                    writeDate(row, 12, report.getBusinessDate());
                    writeDateTime(row, 13, report.getTransactionTime());
                }
        );
    }

    public byte[] exportPaymentReport(
            List<PaymentReportResponse> reports) {

        return createWorkbook(
                "Payment Report",
                "Payments",
                new String[]{
                        "Payment ID",
                        "Sale ID",
                        "Sale Number",
                        "Transaction Reference",
                        "Station ID",
                        "Station",
                        "Pump ID",
                        "Pump Number",
                        "Attendant ID",
                        "Attendant",
                        "Amount",
                        "Payment Method",
                        "Payment Status",
                        "Business Date",
                        "Payment Time"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getPaymentId());
                    writeLong(row, 1, report.getSaleId());
                    writeString(row, 2, report.getSaleNumber());
                    writeString(row, 3, report.getTransactionReference());
                    writeLong(row, 4, report.getStationId());
                    writeString(row, 5, report.getStationName());
                                        writeLong(row, 6, report.getPumpId());
                    writeString(row, 7, report.getPumpNumber());
                    writeLong(row, 8, report.getAttendantId());
                    writeString(row, 9, report.getAttendantName());
                    writeDecimal(row, 10, report.getAmount());
                    writeEnum(row, 11, report.getPaymentMethod());
                    writeEnum(row, 12, report.getPaymentStatus());
                    writeDate(row, 13, report.getBusinessDate());
                    writeDateTime(row, 14, report.getPaymentTime());
                }
        );
    }

    public byte[] exportPriceHistoryReport(
            List<PriceHistoryReportResponse> reports) {

        return createWorkbook(
                "Price History Report",
                "Price History",
                new String[]{
                        "History ID",
                        "Station ID",
                        "Station",
                        "Product ID",
                        "Product",
                        "Old Selling Price",
                        "New Selling Price",
                        "Price Difference",
                        "Changed By",
                        "Business Date",
                        "Changed At"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getHistoryId());
                    writeLong(row, 1, report.getStationId());
                    writeString(row, 2, report.getStationName());
                    writeLong(row, 3, report.getProductId());
                    writeString(row, 4, report.getProductName());
                    writeDecimal(row, 5, report.getOldSellingPrice());
                    writeDecimal(row, 6, report.getNewSellingPrice());
                    writeDecimal(row, 7, report.getPriceDifference());
                    writeString(row, 8, report.getChangedBy());
                    writeDate(row, 9, report.getBusinessDate());
                    writeDateTime(row, 10, report.getChangedAt());
                }
        );
    }

    public byte[] exportProductReport(
            List<ProductReportResponse> reports) {

        return createWorkbook(
                "Product Report",
                "Products",
                new String[]{
                        "Product ID",
                        "Product",
                        "Product Code",
                        "Stations Stocking Product",
                        "Total Quantity",
                        "Average Selling Price",
                        "Inventory Value",
                        "Deliveries",
                        "Transfers",
                        "Adjustments",
                        "Stock Counts"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getProductId());
                    writeString(row, 1, report.getProductName());
                    writeString(row, 2, report.getProductCode());
                    writeLong(row, 3, report.getStationsStockingProduct());
                    writeDecimal(row, 4, report.getTotalQuantity());
                    writeDecimal(row, 5, report.getAverageSellingPrice());
                    writeDecimal(row, 6, report.getInventoryValue());
                    writeLong(row, 7, report.getDeliveries());
                    writeLong(row, 8, report.getTransfers());
                    writeLong(row, 9, report.getAdjustments());
                    writeLong(row, 10, report.getStockCounts());
                }
        );
    }


    public byte[] exportPumpAssignmentReport(
            List<PumpAssignmentReportResponse> reports) {

        return createWorkbook(
                "Pump Assignment Report",
                "Pump Assignments",
                new String[]{
                        "Assignment ID",
                        "Station ID",
                        "Station",
                        "Pump ID",
                        "Pump Number",
                        "Pump Name",
                        "Attendant ID",
                        "Attendant",
                        "Terminal ID",
                        "Terminal Serial Number",
                        "Assignment Date",
                        "Shift",
                        "Active"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getAssignmentId());
                    writeLong(row, 1, report.getStationId());
                    writeString(row, 2, report.getStationName());
                    writeLong(row, 3, report.getPumpId());
                    writeString(row, 4, report.getPumpNumber());
                    writeString(row, 5, report.getPumpName());
                    writeLong(row, 6, report.getAttendantId());
                writeString(row, 7, report.getAttendantName());
                    writeLong(row, 8, report.getTerminalId());
                    writeString(row, 9, report.getTerminalSerialNumber());
                    writeDate(row, 10, report.getAssignmentDate());
                    writeEnum(row, 11, report.getShift());
                    writeBoolean(row, 12, report.getActive());
                }
        );
    }

    public byte[] exportPumpAuditReport(
            List<PumpAuditReportResponse> reports) {

        return createWorkbook(
                "Pump Audit Report",
                "Pump Audits",
                new String[]{
                        "Audit ID",
                        "Assignment ID",
                        "Station ID",
                        "Station",
                        "Pump ID",
                        "Pump Number",
                        "Pump Name",
                        "Attendant ID",
                        "Attendant",
                        "Business Date",
                        "Shift",
                        "Clock In",
                        "Clock Out",
                        "Opening Reading",
                        "Closing Reading",
                        "Total Dispensed"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getAuditId());
                    writeLong(row, 1, report.getAssignmentId());
                    writeLong(row, 2, report.getStationId());
                    writeString(row, 3, report.getStationName());
                    writeLong(row, 4, report.getPumpId());
                    writeString(row, 5, report.getPumpNumber());
                    writeString(row, 6, report.getPumpName());
                    writeLong(row, 7, report.getAttendantId());
                    writeString(row, 8, report.getAttendantName());
                    writeDate(row, 9, report.getBusinessDate());
                    writeEnum(row, 10, report.getShift());
                    writeDateTime(row, 11, report.getClockInTime());
                    writeDateTime(row, 12, report.getClockOutTime());
                    writeDecimal(row, 13, report.getOpeningReading());
                    writeDecimal(row, 14, report.getClosingReading());
                    writeDecimal(row, 15, report.getTotalDispensed());
                }
        );
    }

    public byte[] exportPumpPerformanceReport(List<PumpPerformanceResponse> reports) {

        return createWorkbook(
                "Pump Performance Report",
                "Pump Performance",
                new String[]{
                        "Pump ID",
                        "Pump Number",
                        "Liters",
                        "Revenue"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getPumpId());
                    writeString(row, 1, report.getPumpNumber());
                    writeDecimal(row, 2, report.getLiters());
                    writeDecimal(row, 3, report.getRevenue());
                }
        );
    }

    public byte[] exportPaymentDistributionReport(List<PaymentDistributionResponse> reports) {

        return createWorkbook(
                "Payment Distribution Report",
                "Payment Distribution",
                new String[]{
                        "Payment Method",
                        "Amount"
                },
                reports,
                (row, report) -> {
                    writeString(row, 0, report.getPaymentMethod().name());
                    writeDecimal(row, 1, report.getAmount());
                }
        );
    }

    public byte[] exportPumpReport(
            List<PumpReportResponse> reports) {

        return createWorkbook(
                "Pump Report",
                "Pumps",
                new String[]{
                        "Pump ID",
                        "Pump Number",
                        "Pump Name",
                        "Station",
                        "Product",
                        "Active",
                        "Total Assignments",
                        "Active Assignments",
                        "Audits Completed"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getPumpId());
                    writeString(row, 1, report.getPumpNumber());
                    writeString(row, 2, report.getPumpName());
                    writeString(row, 3, report.getStationName());
                    writeString(row, 4, report.getProductName());
                    writeBoolean(row, 5, report.getActive());
                    writeLong(row, 6, report.getTotalAssignments());
                    writeLong(row, 7, report.getActiveAssignments());
                    writeLong(row, 8, report.getAuditsCompleted());
                }
        );
    }

    public byte[] exportStationReport(
            List<StationReportResponse> reports) {

        return createWorkbook(
                "Station Report",
                "Stations",
                new String[]{
                        "Station ID",
                        "Station",
                        "Total Products",
                        "Total Pumps",
                        "Active Pump Assignments",
                        "Inventory Quantity",
                        "Inventory Value",
                        "Deliveries",
                        "Transfers In",
                        "Transfers Out",
                        "Adjustments",
                        "Stock Counts",
                        "Low Stock Products"
                },
                reports,
                (row, report) -> {
                    writeLong(row, 0, report.getStationId());
                    writeString(row, 1, report.getStationName());
                    writeLong(row, 2, report.getTotalProducts());
                    writeLong(row, 3, report.getTotalPumps());
                    writeLong(row, 4, report.getActivePumpAssignments());
                    writeDecimal(row, 5, report.getInventoryQuantity());
                    writeDecimal(row, 6, report.getInventoryValue());
                    writeLong(row, 7, report.getDeliveries());
                    writeLong(row, 8, report.getTransfersIn());
                    writeLong(row, 9, report.getTransfersOut());
                    writeLong(row, 10, report.getAdjustments());
                    writeLong(row, 11, report.getStockCounts());
                    writeLong(row, 12, report.getLowStockProducts());
                }
        );
    }

    private <T> byte[] createWorkbook(
            String reportTitle,
            String sheetName,
            String[] headers,
            List<T> reports,
            RowWriter<T> rowWriter) {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet = workbook.createSheet(sheetName);

            createHeaderRow(
                    workbook,
                    sheet,
                    headers
            );

            int rowIndex = 1;

            for (T report : reports) {

                Row row = sheet.createRow(rowIndex++);

                rowWriter.write(row, report);
            }

            formatSheet(
                    sheet,
                    headers.length,
                    rowIndex
            );

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to generate " + reportTitle,
                    e
            );
        }
    }

    private void createHeaderRow(
            Workbook workbook,
            Sheet sheet,
            String[] headers) {

        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle =
                workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);

        headerStyle.setFont(font);

        headerStyle.setAlignment(
                HorizontalAlignment.CENTER
        );

        headerStyle.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        for (int i = 0; i < headers.length; i++) {

            Cell cell = headerRow.createCell(i);

            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerStyle);
        }
    }

    private void formatSheet(
            Sheet sheet,
            int columnCount,
            int rowCount) {

        // Keep header visible while scrolling
        sheet.createFreezePane(0, 1);

        // Enable Excel filtering
        if (rowCount > 1) {

            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            rowCount - 1,
                            0,
                            columnCount - 1
                    )
            );
        }

        // Automatically size columns
        for (int i = 0; i < columnCount; i++) {

            sheet.autoSizeColumn(i);

            // Prevent extremely wide columns
            int width = sheet.getColumnWidth(i);

            sheet.setColumnWidth(
                    i,
                    Math.min(width + 500, 15000)
                                );
        }
    }

   @FunctionalInterface
    private interface RowWriter<T> {

        void write(Row row, T data);
    }
}
