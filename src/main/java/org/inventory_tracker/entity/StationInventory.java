package org.inventory_tracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.ProductType;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class StationInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantId;
    private String outletId;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private Double currentQuantity;
    private Double openingQuantity;
    private Double closingQuantity;
    private Double quantitySold;

    private LocalDate businessDate;

   @ManyToOne
   @JoinColumn(name = "station_id")
   private Station station;
}
