package org.inventory_tracker.entity;

import java.time.LocalDateTime;
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

    @Column(nullable = false, unique = true)
    private String tid;

    @Column(nullable = false, unique = true)
    private String terminalSerialNumber;

    private String model;

    private String posType;

    // private String macAddress;

    private String manufacturer;

    private Boolean active;

    private LocalDateTime lastSyncedAt;
}
