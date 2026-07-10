package org.inventory_tracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "station_id",
                                "product_id"
                        }
                )
        }
)
public class StationInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Station owning this inventory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    /**
     * Fuel or Non-Fuel product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal unitPrice;

    /**
     * Current quantity available.
     */
    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal currentQuantity =
            BigDecimal.ZERO;

    /**
     * Current selling price.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sellingPrice =
            BigDecimal.ZERO;

    /**
     * Minimum quantity before alert.
     */
    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal reorderLevel =
            BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;
}
