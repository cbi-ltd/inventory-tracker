package org.inventory_tracker.integration.cams.dto;


import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

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
    /** Contract v1: the duplicate key, the kind of event, the payload version and the CAMS tenant. */
    private String version;
    private String eventId;
    private String eventType;
    private String institutionId;
    private String merchantProfileId;

    private String sessionId;

    private String outletId;

    private String deviceSerial;

    private String virtualAccountNumber;

    private BigDecimal amount;

    private String narration;

    private String paymentStatus;

    private PaymentMethod paymentMethod;

    private Instant paymentTime;

    private String payerName;

    private String payerAccountNumber;

    private String payerBankName;

    private String payerBankCode;

    private String tid;

}