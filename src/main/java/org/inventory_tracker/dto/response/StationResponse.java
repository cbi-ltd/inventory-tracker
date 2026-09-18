package org.inventory_tracker.dto.response;

import lombok.*;
import org.inventory_tracker.enums.TerminalMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationResponse {

    private Long id;

    private String code;

    private String name;

    private String address;

    private String city;

    private String state;

    private String phoneNumber;

    private String email;

    private Boolean active;

    private TerminalMode terminalMode;
}