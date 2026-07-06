package org.inventory_tracker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private String transactionReference;

    private Double amount;

    private String status;

    private String paymentMethod;

    private String attendantName;

    private String stationName;
}
