package org.inventory_tracker.integration.cams.dto;


import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// @Getter
// @Setter
// public class CamsPaymentDTO {

//     private String transactionReference;

//     private String saleNumber;

//     private BigDecimal amount;

//     private PaymentMethod paymentMethod;

//     private PaymentStatus paymentStatus;

//     private String rrn;

//     private String stan;

//     private String terminalSerialNumber;

//     private String merchantId;

//     private String outletId;

//     private String authorizationCode;

//     private String cardScheme;

//     private String responseCode;

//     private String responseMessage;

//     private LocalDateTime paymentTime;
// }


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamsPaymentNotification {

    /**
     * CAMS References
     */
    private String requestReference;

    private String sessionId;

    private String outletId;

    private String deviceSerial;

    private String virtualAccountNumber;

    private BigDecimal amount;

    private String narration;

    private String paymentStatus;

    private PaymentMethod paymentMethod;

    private LocalDateTime paymentTime;

    private String payerName;

    private String payerAccountNumber;

    private String payerBankName;

    private String payerBankCode;

    private String tid;

}