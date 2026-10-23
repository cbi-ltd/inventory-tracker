package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.PaymentStatus;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "offline_payments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_offline_payment_reference",
            columnNames = "reference"
        ),
        @UniqueConstraint(
            name = "uk_offline_payment_sale",
            columnNames = "sale_id"
        )
    }
)
public class OfflinePayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false, unique = true)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "offline_payment_account_id", nullable = false)
    // private String offlinePaymentAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String destinationBankName;

    @Column(nullable = false)
    private String destinationAccountNumber;

    @Column(nullable = false)
    private String destinationAccountName;

    // Provided by the customer; not proof of payment by itself.
    private String customerTransferReference;

    private LocalDateTime receivedAt;

    private String confirmedBy;

    private String rejectionReason;
}
