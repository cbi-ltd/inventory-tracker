package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateSaleRequest {

    @NotNull
    private Long stationId;

    @NotNull
    private Long pumpId;

    private Long terminalId;

    @NotNull
    private Long attendantId;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    private PaymentMethod paymentMethod;

    private String remarks;
}
