package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.DeliveryStatus;


@Getter
@Setter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "deliveryNumber")})
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deliveryNumber;

    @Column(nullable = false)
    private String supplierName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_inventory_id", nullable = false)
    private StationInventory stationInventory;

    @Column(nullable = false, precision = 19, scale = 3, columnDefinition = "numeric(19,3) default 0.000")
    private BigDecimal costPerUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantityDelivered;

    @Column(nullable = true)
    private LocalDate businessDate;

    @Column(nullable = true)
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    private LocalDateTime reversedAt;

    private String reversalReason;

//     @ManyToOne
//     @JoinColumn(name = "reversed_by")
//     private Attendant reversedBy;

    @Column(length = 500)
    private String remarks;
}
