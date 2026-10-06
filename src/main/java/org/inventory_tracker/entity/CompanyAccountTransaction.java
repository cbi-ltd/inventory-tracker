package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.inventory_tracker.enums.CompanyAccountTransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    indexes = {
        @Index(
            name = "idx_company_account_tx_agreement",
            columnList = "fueling_agreement_id"
        ),
        @Index(
            name = "idx_company_account_tx_date",
            columnList = "transaction_date"
        )
    }
)
public class CompanyAccountTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fueling_agreement_id", nullable = false)
    private FuelingAgreement fuelingAgreement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_sale_id")
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyAccountTransactionType type;

    @Column(
        precision = 19,
        scale = 2,
        nullable = false
    )
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    private String reference;

    private String description;
}