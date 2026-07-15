package org.inventory_tracker.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SaleResponse {

    private Long id;

    private String saleNumber;

    private LocalDate businessDate;

    private LocalDateTime saleTime;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpNumber;

    private Long terminalId;
    private String terminalSerialNumber;

    private Long attendantId;
    private String attendantName;

    private Long productId;
    private String productName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal grossAmount;

    private BigDecimal discountAmount;

    private BigDecimal netAmount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private SaleStatus saleStatus;

    private String transactionReference;

    private String receiptNumber;

    private String shift;

    private String remarks;

    private Boolean inventoryUpdated;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
