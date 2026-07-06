package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
public class Attendant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String pin;
    private String fullName;
    private Boolean active = true;

    // @ManyToOne
    // @JoinColumn(name = "pump_id")
    // private Pump pump;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

}