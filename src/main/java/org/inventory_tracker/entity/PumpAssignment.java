package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import org.inventory_tracker.enums.Shift;


@Getter
@Setter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"pump_id", "assignment_date","shift" }) })
@Entity
public class PumpAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pump pump;

    @ManyToOne(optional = false)
    private Attendant attendant;

    @ManyToOne(optional = false)
    private Station station;

    private LocalDate assignmentDate;

    @Enumerated(EnumType.STRING)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id")
    private Terminal terminal;

    // always copied from Pump Entity
    // private String actualTerminalId;
    // private String actualTerminalSerialNumber;

    private Boolean active = true;
}
