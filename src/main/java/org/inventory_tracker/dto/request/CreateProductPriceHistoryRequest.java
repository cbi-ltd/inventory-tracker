package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
public class CreateProductPriceHistoryRequest {

    @NotNull
    private Long stationId;

    @NotNull
    private Long productId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal oldPrice;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal newPrice;

    private String reason;

    @NotBlank
    private String changedBy;
}
