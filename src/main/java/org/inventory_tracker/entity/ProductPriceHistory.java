package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ProductPriceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal oldPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal newPrice;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
