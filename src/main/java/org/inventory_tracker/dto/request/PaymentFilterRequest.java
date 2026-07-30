package org.inventory_tracker.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentFilterRequest {

    private Long stationId;

    private Long terminalId;

    private Long saleId;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private String paymentNumber;

    private String transactionReference;

    private String gatewayReference;

    private String processor;

    private String payerName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}
