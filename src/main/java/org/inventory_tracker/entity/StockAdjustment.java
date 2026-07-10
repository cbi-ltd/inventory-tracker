package org.inventory_tracker.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.enums.StockAdjustmentType;

@Entity
@Getter
@Setter
public class StockAdjustment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String adjustmentNumber;

    @ManyToOne
    @JoinColumn(name = "station_inventory_id", nullable = false)
    private StationInventory stationInventory;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockAdjustmentType adjustmentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_count_id")
    private StockCount stockCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockAdjustmentReason reason;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAdjustmentValue;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal balanceBeforeAdjustment;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal balanceAfterAdjustment;

    @Column(nullable = false)
    private String adjustedBy;

    private String remarks;
}
