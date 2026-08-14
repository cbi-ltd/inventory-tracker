package org.inventory_tracker.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.DeliveryStatus;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReportResponse {

    private Long deliveryId;

    private String deliveryNumber;

    private Long stationId;
    private String stationName;

    private Long productId;
    private String productName;

    private Long stationInventoryId;

    private BigDecimal quantityDelivered;

    private BigDecimal costPerUnit;

    private BigDecimal totalCost;

    private DeliveryStatus status;

    private LocalDate businessDate;

    private LocalDateTime receivedAt;

    private LocalDateTime reversedAt;

    private String remarks;

    private String reversalReason;
}
