package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.InventoryTransactionType;


@Getter
@Setter
@Entity
public class InventoryTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Inventory being affected.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_inventory_id", nullable = false)
    private StationInventory stationInventory;

    /**
     * Convenience references for reporting.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryTransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String referenceNumber;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal balanceBeforeTransaction;

    /**
     * Positive = stock in
     * Negative = stock out
     */
    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal balanceAfterTransaction;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Column(nullable = false)
    private LocalDateTime transactionTime;
}
