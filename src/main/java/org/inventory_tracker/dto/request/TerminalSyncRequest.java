package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerminalSyncRequest {

    @NotBlank(message = "Terminal ID is required")
    private String terminalId;

    @NotBlank(message = "Terminal serial number is required")
    private String terminalSerialNumber;

    private String model;

    private String manufacturer;

    private String posType;

    @NotNull(message = "Active status is required")
    private Boolean active;
}
