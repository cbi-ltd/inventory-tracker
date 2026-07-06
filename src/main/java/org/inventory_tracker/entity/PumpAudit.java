package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PumpAudit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private Integer pumpNumber;
    @ManyToOne
    private Pump pump;

    private String merchantId;

    private String outletId;
    private String attendantId;
    // private String posTerminalId;

    private Double openingReading;
    private Double closingReading;

    private Double totalDispensed;


    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    // @ManyToOne
    // @JoinColumn(name = "attendant_id", insertable = false, updatable = false)
    // private Attendant attendant;

    @ManyToOne
    @JoinColumn(name = "pos_terminal_id")
    private PosTerminal posTerminal;
}