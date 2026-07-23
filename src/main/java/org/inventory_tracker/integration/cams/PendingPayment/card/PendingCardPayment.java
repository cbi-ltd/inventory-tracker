package org.inventory_tracker.integration.cams.PendingPayment.card;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_card_payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCardPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String saleNumber;

    @Column(nullable = false)
    private String terminalId;

    @Column(nullable = false)
    private String terminalSerialNumber;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
