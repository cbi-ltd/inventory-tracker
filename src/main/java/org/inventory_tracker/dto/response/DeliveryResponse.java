package org.inventory_tracker.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.DeliveryStatus;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {

    private Long id;

    private String deliveryNumber;

    private String supplierName;

    private Long stationInventoryId;

    private Long stationId;

    private String stationName;

    private Long productId;

    private String productName;

    private BigDecimal quantityDelivered;

    private DeliveryStatus status;

    private String remarks;

    private LocalDate businessDate;

    private LocalDateTime receivedAt;
}
