package org.inventory_tracker.integration.cams.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentNotification {

    private String terminalSerialNumber;
    /** Contract v1: the duplicate key, the kind of event, the payload version and the CAMS tenant. */
    private String version;
    private String eventId;
    private String eventType;
    private String requestReference;
    private String institutionId;
    private String merchantProfileId;
    private String outletId;
    private String switchTerminalId;

    private String rrn;

    private String stan;

    private String authCode;

    private BigDecimal amount;

    private String merchantId;

    private String merchantName;

    private String responseCode;

    private String responseMessage;

    private String status;
    private String cardScheme;

    private Instant transactionTime;

}
