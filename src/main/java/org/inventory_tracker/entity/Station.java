package org.inventory_tracker.entity;

import java.time.ZoneId;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Station extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;

    private String state;

    private String phoneNumber;

    private String email;

    @Column(nullable =false)
    private Boolean active = true;

    @Column(nullable = false)
    private ZoneId timeZone;
}
