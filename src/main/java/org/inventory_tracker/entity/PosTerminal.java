package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PosTerminal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;

    private String macAddress;

    private String posType;

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;
}
