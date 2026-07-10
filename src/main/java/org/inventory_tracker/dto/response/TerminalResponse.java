package org.inventory_tracker.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalResponse {

    private Long id;

    private String terminalId;

    private String terminalSerialNumber;

    private String manufacturer;

    private String model;

    private String posType;

    private Boolean active;

    private LocalDateTime lastSyncedAt;
}