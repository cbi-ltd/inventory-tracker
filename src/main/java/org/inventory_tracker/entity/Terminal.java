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
    private String tid; //FOR NOW, SAVE SERIAL-NUMBER INTO THIS

    @Column(nullable = false, unique = true)
    private String terminalSerialNumber;

    private String model;

    private String posType;

    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "merchant_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private String manufacturer;

    private Boolean active;

    private LocalDateTime lastSyncedAt;
}
