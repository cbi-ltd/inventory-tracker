package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CreateReconciliationRequest {
    
    @NotNull 
    private Long offlinePaymentId;

    @NotBlank
    private String transferReference;

    @NotBlank 
    private String destinationAccountNumber;
}
