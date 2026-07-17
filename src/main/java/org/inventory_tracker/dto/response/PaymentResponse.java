package org.inventory_tracker.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long id;

    private String paymentNumber;

    private Long saleId;

    private String saleNumber;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionReference;

    private String rrn;

    private String stan;

    private Long terminalId;

    private String terminalSerialNumber;

    private String merchantId;

    private String outletId;

    private String authorizationCode;

    private String cardScheme;

    private String responseCode;

    private String responseMessage;

    private LocalDateTime paymentTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
