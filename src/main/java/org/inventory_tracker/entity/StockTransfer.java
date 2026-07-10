package org.inventory_tracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.StockTransferStatus;


@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "transferNumber")
        }
)
public class StockTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Business reference.
     */
    @Column(nullable = false, unique = true)
    private String transferNumber;

    /**
     * Source inventory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_inventory_id", nullable = false)
    private StationInventory sourceInventory;

    /**
     * Destination inventory.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_inventory_id", nullable = false)
    private StationInventory destinationInventory;

    /*
     * Convenience references.
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    @Column(nullable = false)
    private String initiatedBy;

    @Column
    private String completedBy;

    private BigDecimal sourceBalanceBeforeTransfer;
    private BigDecimal sourceBalanceAfterTransfer;

    private BigDecimal destinationBalanceBeforeTransfer;
    private BigDecimal destinationBalanceAfterTransfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal unitPrice;

    private BigDecimal totalValueTransferred;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantityTransferred;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTransferStatus status;

    @Column(nullable = false)
    private LocalDate businessDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_out_transaction_id")
    private InventoryTransaction transferOutTransaction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_in_transaction_id")
    private InventoryTransaction transferInTransaction;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String remarks;
}
