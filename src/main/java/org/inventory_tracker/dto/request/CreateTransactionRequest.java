package org.inventory_tracker.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.TransactionStatus;
import org.inventory_tracker.enums.TransactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    private Double amount;

    private String narration;

    private TransactionType transactionType;

    private TransactionStatus status;

    private PaymentMethod paymentMethod;

    private Long stationId;

    private Long attendantId;

    private Long businessTypeId;

    private Long posTerminalId;
}
