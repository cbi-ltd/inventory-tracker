package org.inventory_tracker.dto.response.report;

import org.inventory_tracker.enums.PaymentMethod;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDistributionResponse {

    private PaymentMethod paymentMethod;
    private BigDecimal amount;
}
