// package org.inventory_tracker.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "price_history")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class PriceHistory {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(
//             name = "station_inventory_id",
//             nullable = false
//     )
//     private StationInventory stationInventory;

//     @Column(nullable = false, precision = 19, scale = 2)
//     private BigDecimal oldPrice;

//     @Column(nullable = false, precision = 19, scale = 2)
//     private BigDecimal newPrice;

//     @Column(length = 255)
//     private String reason;

//     @Column(nullable = false)
//     private String changedBy;

//     @Column(nullable = false)
//     private LocalDateTime effectiveDate;

//     @PrePersist
//     public void prePersist() {

//         if (effectiveDate == null) {
//             effectiveDate = LocalDateTime.now();
//         }
//     }
// }
