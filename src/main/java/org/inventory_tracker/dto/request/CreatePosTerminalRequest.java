package org.inventory_tracker.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePosTerminalRequest {

    private String serialNumber;

    private String macAddress;

    private String posType;

    private Long stationId;
}