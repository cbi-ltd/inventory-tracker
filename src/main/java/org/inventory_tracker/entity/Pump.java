package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Pump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pumpNumber;
    private String pumpName;
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_terminal_id")
    private Terminal defaultTerminal;

    private String terminalSerialNumber;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;
}
