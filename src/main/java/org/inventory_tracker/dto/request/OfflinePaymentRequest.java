package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter 
@Setter
public class OfflinePaymentRequest {
    
    @NotNull
    private Long stationId;

    @NotBlank 
    private String bankName;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountName;
}
