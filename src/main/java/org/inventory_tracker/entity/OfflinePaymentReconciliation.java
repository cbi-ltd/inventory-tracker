package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.ReconciliationStatus;


@Entity
@Getter 
@Setter
@Builder 
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "offline_payment_reconciliation",
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_offline_payment_reconciliation_reference",
               columnNames = "reference"
           )
       }
)
public class OfflinePaymentReconciliation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offline_payment_id", nullable = false)
    private OfflinePayment offlinePayment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String transferReference;

    @Column(nullable = false)
    private String sourceAccountNumber;

    @Column(nullable = false)
    private String destinationAccountNumber;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private ReconciliationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status;

    private String initiatedBy;
    private String confirmedBy;

    private LocalDateTime initiatedAt;
    private LocalDateTime confirmedAt;

    private String failureReason;
}
