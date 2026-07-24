package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.DeliveryStatus;


@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "deliveryNumber")
        }
)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business document number.
     */
    @Column(nullable = false, unique = true)
    private String deliveryNumber;

    /**
     * Supplier delivering the product.
     */
    @Column(nullable = false)
    private String supplierName;

    /**
     * Inventory receiving the stock.
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

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantityDelivered;

    @Column(nullable = true)
    private LocalDate businessDate;

    @Column(nullable = true)
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(length = 500)
    private String remarks;
}
