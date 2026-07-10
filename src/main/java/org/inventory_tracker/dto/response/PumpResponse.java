package org.inventory_tracker.dto.response;

import lombok.*;
import org.inventory_tracker.entity.Product;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PumpResponse {

    private Long id;

    private String pumpNumber;

    private String pumpName;

    private Boolean active;

    private Long defaultTerminalDbId;

    private String terminalId;

    private String terminalSerialNumber;

    private Long productId;
    
    private String productName;

    private Long stationId;

    private String stationName;
}
