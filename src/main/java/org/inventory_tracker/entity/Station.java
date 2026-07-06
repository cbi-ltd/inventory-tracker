package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String email;

    private String phone;

    private Double totalCapacity;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "station")
    private List<Attendant> attendants;

    @OneToMany(mappedBy = "station")
    private List<PosTerminal> terminals;

    @OneToMany(mappedBy = "station")
    private List<StationInventory> inventories;
}
