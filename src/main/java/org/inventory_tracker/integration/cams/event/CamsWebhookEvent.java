package org.inventory_tracker.integration.cams.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One row per CAMS notice that was handled. The unique eventId is what makes a repeated delivery harmless.
 * The raw body is kept only when nothing was matched (IGNORED or AMOUNT_MISMATCH), so staff can reconcile it.
 */
@Entity
@Table(name = "cams_webhook_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CamsWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String eventId;

    /** TRANSFER or CARD. */
    @Column(nullable = false, length = 20)
    private String kind;

    /** PROCESSED, IGNORED or AMOUNT_MISMATCH. */
    @Column(nullable = false, length = 30)
    private String outcome;

    private String terminalSerial;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 300)
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String rawBody;

    @Column(nullable = false)
    private LocalDateTime receivedAt;
}
