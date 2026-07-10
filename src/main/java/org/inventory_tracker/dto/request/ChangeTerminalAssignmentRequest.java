package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ChangeTerminalAssignmentRequest {

    @NotNull
    private Long terminalId;

    private String reason;
}
