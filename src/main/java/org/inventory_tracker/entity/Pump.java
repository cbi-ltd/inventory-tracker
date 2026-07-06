package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.ProductType;

@Entity
@Getter
@Setter
public class Pump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer pumpNumber;
    private String name;
    private Boolean active = true;
    private String outletId;
    private String assignedStaffId;
    @ManyToOne
    @JoinColumn(name = "business_type_id")
    private BusinessType businessType;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @ManyToOne
    private Station station;
}
