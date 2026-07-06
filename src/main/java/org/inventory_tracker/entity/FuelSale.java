package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.TransactionStatus;

@Getter
@Setter
@Entity
public class FuelSale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Pump pump;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @ManyToOne
    private PumpAudit pumpAudit;

    private Double litres;

    private Double pricePerLitre;

    private Double totalAmount;

    // CAMS references
    private String merchantId;
    private String outletId;
    private String attendantId;

    // Optional link to CAMS payment transaction
    private String transactionReference;
    private String posTerminalId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}