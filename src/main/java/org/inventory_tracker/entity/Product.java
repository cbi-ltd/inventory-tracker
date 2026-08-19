package org.inventory_tracker.entity;

import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.UnitOfMeasure;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(name = "uk_product_product_type", columnNames = "product_type") })
public class Product {    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Transient
    public String getProductCode() {
        return productType != null ? productType.getCode() : null;
    }

    @Transient
    public String getProductDescription() {
        return productType != null ? productType.getDescription() : null;
    }

    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(nullable = false)
    private Boolean active = true;

    private String description;
}
