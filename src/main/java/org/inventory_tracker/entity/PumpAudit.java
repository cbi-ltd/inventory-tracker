package org.inventory_tracker.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class PumpAudit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pump_assignment_id", nullable = false)
    private PumpAssignment pumpAssignment;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal openingReading;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal closingReading;

    private BigDecimal totalDispensed;

    @Column(nullable = false)
    private LocalDateTime clockInTime;

    private LocalDateTime clockOutTime;

    @Column(nullable = false)
    private LocalDate businessDate;

    @Column(length = 500)
    private String remarks;

    // private Integer pumpNumber;
    // private String posTerminalId;
    // @ManyToOne
    // private Pump pump;
    // private String merchantId;
    // private String outletId;
    // private String attendantId;
        // @ManyToOne
    // @JoinColumn(name = "station_id")
    // private Station station;

    // @ManyToOne
    // @JoinColumn(name = "attendant_id", insertable = false, updatable = false)
    // private Attendant attendant;

    // @ManyToOne
    // @JoinColumn(name = "pos_terminal_id")
    // private PosTerminal posTerminal;
}