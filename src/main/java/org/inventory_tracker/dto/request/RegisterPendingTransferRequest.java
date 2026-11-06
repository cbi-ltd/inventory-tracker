package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/** What the POS sends to register a transfer payment. Unknown fields (id, createdAt, tid) are ignored. */
@Getter
@Setter
public class RegisterPendingTransferRequest {

    @NotBlank
    private String virtualAccountNumber;

    @NotBlank
    private String saleNumber;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;

    @NotBlank
    private String deviceSerial;
}
