package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosTerminalResponse {

    private Long id;

    private String serialNumber;

    private String macAddress;

    private String posType;

    private Boolean active;

    private Long stationId;

    private String stationName;
}