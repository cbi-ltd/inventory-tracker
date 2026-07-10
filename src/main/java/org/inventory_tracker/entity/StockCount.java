package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class StockCount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String countNumber;

    @ManyToOne
    @JoinColumn(name = "station_inventory_id", nullable = false)
    private StationInventory stationInventory;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // @OneToMany(mappedBy = "stockCount", fetch = FetchType.LAZY)
    // private List<StockAdjustment> stockAdjustments = new ArrayList<>();

    @OneToOne(mappedBy = "stockCount", fetch = FetchType.LAZY)
    private StockAdjustment stockAdjustment;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal systemQuantity;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal countedQuantity;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal variance;

    @Column(nullable = false)
    private String countedBy;

    private String remarks;
}
