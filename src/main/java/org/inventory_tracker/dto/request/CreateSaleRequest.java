package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.AssertTrue;
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
    private Long pumpId;

    @DecimalMin("0.001")
    private BigDecimal quantity;

    @DecimalMin("0.001")
    private BigDecimal amount;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    private PaymentMethod paymentMethod;

    @AssertTrue(message = "Either quantity or amount must be provided, but not both or neither")
    private boolean isValidQuantityOrAmount() {
        boolean hasQuantity = quantity != null;
        boolean hasAmount = amount != null;
        
        return hasQuantity ^ hasAmount; 
    }

    private String remarks;

    // private Long terminalId;

    // @NotNull
    // private Long stationId;

    // @NotNull
    // private Long attendantId;

    // @NotNull
    // private Long productId;

    // private BigDecimal unitPrice;
}
