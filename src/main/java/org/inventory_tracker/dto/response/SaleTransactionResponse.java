package org.inventory_tracker.dto.response;

import lombok.*;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.TransactionStatus;
import org.inventory_tracker.enums.TransactionType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleTransactionResponse {

    private Long id;

    private String transactionReference;

    private TransactionType transactionType;

    private PaymentMethod paymentMethod;

    private TransactionStatus status;

    private Double amount;

    private String narration;

    private Long stationId;

    private String stationName;

    private Long attendantId;

    private String attendantName;

    private Long businessTypeId;

    private String businessTypeName;

    private Long posTerminalId;

    private String terminalSerialNumber;
}