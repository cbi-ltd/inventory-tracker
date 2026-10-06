package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.inventory_tracker.enums.FuelingSettlementType;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    indexes = {
        @Index(name = "idx_fueling_agreement_company", columnList = "company_id"),
        @Index(name = "idx_fueling_agreement_period", columnList = "start_date,end_date")
    }
)
public class FuelingAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private FuelingCompany company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelingSettlementType settlementType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal prepaidBalance = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal creditLimit;

    @Column(nullable = false)
    private Boolean active = true;

    private String reference;

    private String remarks;
}
