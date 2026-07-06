package org.inventory_tracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.TransactionStatus;
import org.inventory_tracker.enums.TransactionType;

@Getter
@Setter
@Entity
public class SaleTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private Double amount;
    private String narration;

    @ManyToOne
    private Pump pump;

    private Double quantity;
    private Double pricePerUnit;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "attendant_id")
    private Attendant attendant;

    @ManyToOne
    @JoinColumn(name = "business_category_id")
    private BusinessType businessType;

    @ManyToOne
    @JoinColumn(name = "pos_terminal_id")
    private PosTerminal posTerminal;
}
