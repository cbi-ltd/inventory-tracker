package org.inventory_tracker.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Terminal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String tid; //FOR NOW, SAVE SERIAL-NUMBER INTO THIS

    @Column(nullable = false, unique = true)
    private String terminalSerialNumber;

    @OneToMany(mappedBy = "defaultTerminal", fetch = FetchType.LAZY)
    private List<Pump> pumps = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    private String model;

    private String posType;

    private String manufacturer;

    private Boolean active;

    private LocalDateTime lastSyncedAt;
}
