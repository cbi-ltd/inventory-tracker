package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
public class CreatePumpRequest {

    @NotBlank(message = "Pump number is required")
    private String pumpNumber;

    @NotBlank(message = "Pump name is required")
    private String pumpName;

    private Long defaultTerminalId;

    private String terminalSerialNumber;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Station ID is required")
    private Long stationId;
}
