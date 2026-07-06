package org.inventory_tracker.entity;

import jakarta.persistence.*;
import org.inventory_tracker.enums.InventoryMovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InventoryMovementType type;

    private BigDecimal quantity; // litres (+/-)

    private BigDecimal unitPrice;

    private BigDecimal totalValue;

    private LocalDateTime createdAt;

    private String referenceId;
    // e.g. FuelSale ID, Adjustment ID, CAMS transaction id

    private String description;


    @ManyToOne(fetch = FetchType.LAZY)
    private Station station;
}
