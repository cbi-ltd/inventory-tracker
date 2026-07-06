package org.inventory_tracker.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PumpResponse {
    private Long id;
    private Integer pumpNumber;
    private Boolean active;
    private Long stationId;
    private String stationName;
}
