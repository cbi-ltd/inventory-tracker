package org.inventory_tracker.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePumpRequest {

    private String pumpNumber;

    private String pumpName;

    private Long stationId;

    private Long productId;

    private Long defaultTerminalId;
}
