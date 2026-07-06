package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;

@Getter
@Setter
public class CreateFuelSaleRequest {

    private Long pumpId;

    private Long pumpAuditId;

    private Double litres;

    private PaymentMethod paymentMethod;

    private String transactionReference;

    private String attendantId;

    private String merchantId;

    private String outletId;
}
