package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.util.ShiftUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        indexes = {
                @Index(name = "idx_sale_number", columnList = "saleNumber", unique = true),
                @Index(name = "idx_sale_business_date", columnList = "businessDate"),
                @Index(name = "idx_sale_station", columnList = "station_id"),
                @Index(name = "idx_sale_attendant", columnList = "attendant_id"),
                @Index(name = "idx_sale_terminal", columnList = "terminal_id")
        }
)
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String saleNumber;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Column(nullable = false)
    private LocalDateTime saleTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_id", nullable = false)
    private Pump pump;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id")
    private Terminal terminal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendant_id", nullable = false)
    private Attendant attendant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Column(nullable = false)
    private Boolean inventoryUpdated = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus saleStatus;

    @Column(unique = true)
    private String transactionReference;

    @Column(unique = true)
    private String receiptNumber;

    @Column(nullable = false)
    private String shift;

    @Column(length = 1000)
    private String remarks;

    @PrePersist
    public void prePersist() {

        if (saleTime == null) {
            saleTime = LocalDateTime.now();
        }

        if (businessDate == null) {
            businessDate = saleTime.toLocalDate();
        }

        if (shift == null) {
            shift = ShiftUtil.determineShift(saleTime.toLocalTime()).name();
        }

        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }

        if (grossAmount != null && netAmount == null) {
            netAmount = grossAmount.subtract(discountAmount);
        }
    }
}
