package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BusinessType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double globalPricePerUnit;
    private Double stationPricePerUnit;

//    @ManyToOne
//    @JoinColumn(name = "company_id")
//    private Company company;
//
//    @ManyToOne
//    @JoinColumn(name = "station_id")
//    private Station station;
}