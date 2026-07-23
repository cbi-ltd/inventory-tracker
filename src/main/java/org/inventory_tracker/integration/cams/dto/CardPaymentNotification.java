package org.inventory_tracker.integration.cams.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentNotification {

    private String terminalId;

    private String rrn;

    private String stan;

    private String authCode;

    private BigDecimal amount;

    private String merchantId;

    private String merchantName;

    private String responseCode;

    private String responseMessage;

    private String status;

    private LocalDateTime transactionTime;

}
