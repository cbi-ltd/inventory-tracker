package org.inventory_tracker.service;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

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
import org.inventory_tracker.dto.response.report.SalesReportResponse;
import org.inventory_tracker.dto.response.report.StationReportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.awt.Color;

@Service
public class PdfReportService {

    public byte[] createPdf(
            String title,
            String[] headers,
            List<?> reports,
            BiConsumer<PdfPTable, Object> rowWriter) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    16
            );

            document.add(new Paragraph(title, titleFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(
                        new Phrase(header)
                );
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (Object report : reports) {
                rowWriter.accept(table, report);
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();
        } 
        catch (DocumentException | IOException e) {
            throw new IllegalStateException(
                    "Failed to generate PDF report",
                    e
            );
        }
    }



    public byte[] exportAttendantReport(List<AttendantReportResponse> reports) {

        return createPdf(
                "Attendant Report",
                new String[]{
                        "ID",
                        "Username",
                        "Full Name",
                        "Station",
                        "Pump",
                        "Active",
                        "Completed Shifts",
                        "Stock Counts",
                        "Adjustments",
                        "Deliveries",
                        "Transfers"
                },
                reports,
                (table, report) -> {
                    AttendantReportResponse r =
                            (AttendantReportResponse) report;

                    table.addCell(String.valueOf(r.getAttendantId()));
                    table.addCell(r.getUsername());
                    table.addCell(r.getFullName());
                    table.addCell(r.getStationName());
                    table.addCell(r.getAssignedPump());
                    table.addCell(String.valueOf(r.getActive()));
                    table.addCell(String.valueOf(r.getCompletedShifts()));
                    table.addCell(String.valueOf(r.getStockCountsPerformed()));
                    table.addCell(String.valueOf(r.getAdjustmentsPerformed()));
                    table.addCell(String.valueOf(r.getDeliveriesReceived()));
                    table.addCell(String.valueOf(r.getTransfersInitiated()));
                }
        );
    }

    public byte[] exportDeliveryReport(List<DeliveryReportResponse> reports) { 
        return createPdf( "Delivery Report", new String[]{ "ID", "Delivery No.", "Station", "Product", "Quantity", "Cost/Unit", "Total Cost", "Status", "Business Date", "Received At", "Reversed At", "Remarks", "Reversal Reason" }, 
                    reports, 
                    (table, report) -> { DeliveryReportResponse r = (DeliveryReportResponse) report;
             table.addCell(String.valueOf(r.getDeliveryId())); 
             table.addCell(r.getDeliveryNumber()); 
             table.addCell(r.getStationName()); 
             table.addCell(r.getProductName()); table.addCell(String.valueOf(r.getQuantityDelivered())); 
             table.addCell(String.valueOf(r.getCostPerUnit())); 
             table.addCell(String.valueOf(r.getTotalCost())); 
             table.addCell(String.valueOf(r.getStatus())); 
             table.addCell(String.valueOf(r.getBusinessDate())); 
             table.addCell(String.valueOf(r.getReceivedAt())); 
             table.addCell(String.valueOf(r.getReversedAt())); 
             table.addCell(r.getRemarks());
            table.addCell(r.getReversalReason()); 
        } ); 
    }


    public byte[] exportInventoryReport(List<InventoryReportResponse> reports) { 
        return createPdf( "Inventory Report", new String[]{ "Station", "Product", "Current Quantity", "Selling Price", "Inventory Value", "Reorder Level", "Below Reorder Level" }, 
        reports,
        (table, report) -> { InventoryReportResponse r = (InventoryReportResponse) report; 
            
            table.addCell(r.getStationName()); 
            table.addCell(r.getProductName()); 
            table.addCell(String.valueOf(r.getCurrentQuantity())); 
            table.addCell(String.valueOf(r.getSellingPrice())); 
            table.addCell(String.valueOf(r.getInventoryValue())); 
            table.addCell(String.valueOf(r.getReorderLevel())); 
            table.addCell(String.valueOf(r.getBelowReorderLevel())); 
        } ); 
    }

    public byte[] exportInventoryTransactionReport( List<InventoryTransactionReportResponse> reports) { 
        return createPdf( "Inventory Transaction Report", new String[]{ "Transaction ID", "Station", "Product", "Transaction Type", "Quantity", "Balance Before", "Balance After", "Remarks", "Reference No.", "Business Date", "Transaction Time" }, 
        reports, 
        (table, report) -> { InventoryTransactionReportResponse r = (InventoryTransactionReportResponse) report; 
            
            table.addCell(String.valueOf(r.getTransactionId())); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getProductName()); 
            table.addCell(String.valueOf(r.getTransactionType())); 
            table.addCell(String.valueOf(r.getQuantity())); 
            table.addCell(String.valueOf(r.getBalanceBeforeTransaction())); 
            table.addCell(String.valueOf(r.getBalanceAfterTransaction())); 
            table.addCell(r.getRemarks()); table.addCell(r.getReferenceNumber()); 
            table.addCell(String.valueOf(r.getBusinessDate())); 
            table.addCell(String.valueOf(r.getTransactionTime())); 
        } ); 
    }

    public byte[] exportPaymentReport(List<PaymentReportResponse> reports) { 
        return createPdf( "Payment Report", new String[]{ "Payment ID", "Sale ID", "Sale No.", "Transaction Ref.", "Station", "Pump", "Attendant", "Amount", "Payment Method", "Payment Status", "Business Date", "Payment Time" }, 
        reports, 
        (table, report) -> { PaymentReportResponse r = (PaymentReportResponse) report; 
            
            table.addCell(String.valueOf(r.getPaymentId())); 
            table.addCell(String.valueOf(r.getSaleId())); 
            table.addCell(r.getSaleNumber()); 
            table.addCell(r.getTransactionReference()); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getPumpNumber()); 
            table.addCell(r.getAttendantName()); 
            table.addCell(String.valueOf(r.getAmount())); 
            table.addCell(String.valueOf(r.getPaymentMethod())); 
            table.addCell(String.valueOf(r.getPaymentStatus())); 
            table.addCell(String.valueOf(r.getBusinessDate())); 
            table.addCell(String.valueOf(r.getPaymentTime())); 
        } ); 
    }

    public byte[] exportPaymentDistributionReport( List<PaymentDistributionResponse> reports) { 
        return createPdf( "Payment Distribution Report", new String[]{ "Payment Method", "Amount" }, 
            reports, 
            (table, report) -> { PaymentDistributionResponse r = (PaymentDistributionResponse) report; 
            
            table.addCell( r.getPaymentMethod() != null ? r.getPaymentMethod().name() : "" ); 
            table.addCell(String.valueOf(r.getAmount())); } ); 
    }

    public byte[] exportPriceHistoryReport(List<PriceHistoryReportResponse> reports) {
        return createPdf(
                "Price History Report",
                new String[]{
                        "History ID", "Station", "Product",
                        "Old Price", "New Price", "Difference",
                        "Changed By", "Business Date", "Changed At"
                },
                reports,
                (table, report) -> {
                    PriceHistoryReportResponse r = (PriceHistoryReportResponse) report;

                    table.addCell(String.valueOf(r.getHistoryId()));
                    table.addCell(r.getStationName());
                    table.addCell(r.getProductName());
                    table.addCell(String.valueOf(r.getOldSellingPrice()));
                    table.addCell(String.valueOf(r.getNewSellingPrice()));
                    table.addCell(String.valueOf(r.getPriceDifference()));
                    table.addCell(r.getChangedBy());
                    table.addCell(String.valueOf(r.getBusinessDate()));
                    table.addCell(String.valueOf(r.getChangedAt()));
                }
        );
    }

    public byte[] exportProductReport(List<ProductReportResponse> reports) { 
        return createPdf( "Product Report", new String[]{ "Product", "Code", "Stations", "Total Quantity", "Avg. Selling Price", "Inventory Value", "Deliveries", "Transfers", "Adjustments", "Stock Counts" }, 
        reports, 
        (table, report) -> { ProductReportResponse r = (ProductReportResponse) report; 
            table.addCell(r.getProductName()); table.addCell(r.getProductCode()); 
            table.addCell(String.valueOf(r.getStationsStockingProduct())); 
            table.addCell(String.valueOf(r.getTotalQuantity())); 
            table.addCell(String.valueOf(r.getAverageSellingPrice())); 
            table.addCell(String.valueOf(r.getInventoryValue())); 
            table.addCell(String.valueOf(r.getDeliveries())); 
            table.addCell(String.valueOf(r.getTransfers())); 
            table.addCell(String.valueOf(r.getAdjustments())); 
            table.addCell(String.valueOf(r.getStockCounts())); } 
        ); 
    }

    public byte[] exportPumpAssignmentReport( List<PumpAssignmentReportResponse> reports) { 
        return createPdf( "Pump Assignment Report", new String[]{ "Assignment ID", "Station", "Pump", "Pump Name", "Attendant", "Terminal ID", "Terminal Serial", "Assignment Date", "Shift", "Active" }, 
        reports, 
        (table, report) -> { PumpAssignmentReportResponse r = (PumpAssignmentReportResponse) report; 
            table.addCell(String.valueOf(r.getAssignmentId())); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getPumpNumber()); 
            table.addCell(r.getPumpName()); 
            table.addCell(r.getAttendantName()); 
            table.addCell(String.valueOf(r.getTerminalId())); 
            table.addCell(r.getTerminalSerialNumber()); 
            table.addCell(String.valueOf(r.getAssignmentDate())); 
            table.addCell(String.valueOf(r.getShift())); 
            table.addCell(String.valueOf(r.getActive())); } );
    }

    public byte[] exportPumpAuditReport( List<PumpAuditReportResponse> reports) { 
        return createPdf( "Pump Audit Report", new String[]{ "Audit ID", "Assignment ID", "Station", "Pump", "Attendant", "Business Date", "Shift", "Clock In", "Clock Out", "Opening Reading", "Closing Reading", "Total Dispensed" }, 
        reports, 
        (table, report) -> { PumpAuditReportResponse r = (PumpAuditReportResponse) report; 
            table.addCell(String.valueOf(r.getAuditId())); 
            table.addCell(String.valueOf(r.getAssignmentId())); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getPumpNumber()); 
            table.addCell(r.getAttendantName()); 
            table.addCell(String.valueOf(r.getBusinessDate())); 
            table.addCell(String.valueOf(r.getShift())); 
            table.addCell(String.valueOf(r.getClockInTime())); 
            table.addCell(String.valueOf(r.getClockOutTime())); 
            table.addCell(String.valueOf(r.getOpeningReading())); 
            table.addCell(String.valueOf(r.getClosingReading())); 
            table.addCell(String.valueOf(r.getTotalDispensed())); } ); 
    }

    public byte[] exportPumpPerformanceReport( List<PumpPerformanceResponse> reports) { 
        return createPdf( "Pump Performance Report", new String[]{ "Pump ID", "Pump Number", "Liters", "Revenue" }, 
        reports, 
        (table, report) -> { PumpPerformanceResponse r = (PumpPerformanceResponse) report; 
            table.addCell(String.valueOf(r.getPumpId())); table.addCell(r.getPumpNumber()); 
            table.addCell(String.valueOf(r.getLiters())); 
            table.addCell(String.valueOf(r.getRevenue())); } ); 
    }


    public byte[] exportPumpReport(List<PumpReportResponse> reports) { 
        return createPdf( "Pump Report", new String[]{ "Pump ID", "Pump Number", "Pump Name", "Station", "Product", "Active", "Total Assignments", "Active Assignments", "Audits Completed" }, 
        reports, 
        (table, report) -> { PumpReportResponse r = (PumpReportResponse) report; 
            table.addCell(String.valueOf(r.getPumpId())); 
            table.addCell(r.getPumpNumber()); 
            table.addCell(r.getPumpName()); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getProductName()); 
            table.addCell(String.valueOf(r.getActive())); 
            table.addCell(String.valueOf(r.getTotalAssignments())); 
            table.addCell(String.valueOf(r.getActiveAssignments())); 
            table.addCell(String.valueOf(r.getAuditsCompleted())); } ); 
    }

    public byte[] exportStationReport(List<StationReportResponse> reports) { 
        return createPdf( "Station Report", new String[]{ "Station", "Products", "Pumps", "Active Assignments", "Inventory Quantity", "Inventory Value", "Deliveries", "Transfers In", "Transfers Out", "Adjustments", "Stock Counts", "Low Stock Products" }, 
        reports, 
        (table, report) -> { StationReportResponse r = (StationReportResponse) report; 
            table.addCell(r.getStationName()); 
            table.addCell(String.valueOf(r.getTotalProducts())); 
            table.addCell(String.valueOf(r.getTotalPumps())); 
            table.addCell(String.valueOf(r.getActivePumpAssignments())); 
            table.addCell(String.valueOf(r.getInventoryQuantity())); 
            table.addCell(String.valueOf(r.getInventoryValue())); 
            table.addCell(String.valueOf(r.getDeliveries())); 
            table.addCell(String.valueOf(r.getTransfersIn()));
            table.addCell(String.valueOf(r.getTransfersOut())); 
            table.addCell(String.valueOf(r.getAdjustments())); 
            table.addCell(String.valueOf(r.getStockCounts())); 
            table.addCell(String.valueOf(r.getLowStockProducts())); } ); 
    }

    public byte[] exportSalesReport(List<SalesReportResponse> reports) { 
        return createPdf( "Sales Report", new String[]{ "Sale ID", "Sale No.", "Station", "Pump", "Product", "Attendant", "Business Date", "Sale Time", "Shift", "Quantity", "Unit Price", "Gross", "Discount", "Net", "Payment Method", "Payment Status", "Sale Status", "Transaction Ref." }, 
        reports, 
        (table, report) -> { SalesReportResponse r = (SalesReportResponse) report; 
            table.addCell(String.valueOf(r.getSaleId())); 
            table.addCell(r.getSaleNumber()); 
            table.addCell(r.getStationName()); 
            table.addCell(r.getPumpNumber()); 
            table.addCell(r.getProductName()); 
            table.addCell(r.getAttendantName()); 
            table.addCell(String.valueOf(r.getBusinessDate())); 
            table.addCell(String.valueOf(r.getSaleTime())); 
            table.addCell(String.valueOf(r.getShift())); 
            table.addCell(String.valueOf(r.getQuantity()));
            table.addCell(String.valueOf(r.getUnitPrice())); 
            table.addCell(String.valueOf(r.getGrossAmount())); 
            table.addCell(String.valueOf(r.getDiscountAmount())); 
            table.addCell(String.valueOf(r.getNetAmount())); 
            table.addCell(String.valueOf(r.getPaymentMethod())); 
            table.addCell(String.valueOf(r.getPaymentStatus())); 
            table.addCell(String.valueOf(r.getSaleStatus())); 
            table.addCell(r.getTransactionReference()); } ); 
    }

}
