package org.inventory_tracker.entity;

import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.UnitOfMeasure;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
public class Product {
    // we will make Price belongs to Inventory and PriceHistory instead of Product
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(nullable = false)
    private Boolean active = true;

    private String description;
}
