package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateAdminRequest {

    private String fullName;
    private String email;
    private String password;
    private Long companyId;
    private Long stationId;
}
