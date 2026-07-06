package org.inventory_tracker.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminResponse {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private Boolean active;

    private Long companyId;

    private String companyName;

    private Long stationId;

    private String stationName;
}
