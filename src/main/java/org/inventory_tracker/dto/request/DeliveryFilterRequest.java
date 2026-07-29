package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.DeliveryStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DeliveryFilterRequest {

    private String deliveryNumber;

    private Long stationId;

    private Long productId;

    private Long stationInventoryId;

    private DeliveryStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}
