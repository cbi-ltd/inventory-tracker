package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "idx_payment_reference", columnList = "transactionReference",unique = true),
                  @Index(name = "idx_payment_sale", columnList = "sale_id")})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(unique = true)
    private String transactionReference;

    /**
     * Retrieval Reference Number.
     */
    private String rrn;

    /**
     * System Trace Audit Number.
     */
    private String stan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id")
    private Terminal terminal;

    private String merchantId;

    private String outletId;

    private String authorizationCode;

    private String cardScheme;

    private String responseCode;

    @Column(length = 500)
    private String responseMessage;

    @Column(nullable = false)
    private LocalDateTime paymentTime;
}
