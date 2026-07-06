package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Company extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String email;

    private String phone;

    private Boolean franchise;

    private String operatorName;

    @OneToMany(mappedBy = "company")
    private List<Station> stations;
}
