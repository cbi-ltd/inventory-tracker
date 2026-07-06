package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendantResponse {

    private Long id;

    private String username;

    private String fullName;

    private Boolean active;

    private Long stationId;

    private String stationName;
}
