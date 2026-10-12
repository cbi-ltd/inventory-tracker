package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;  
import lombok.Setter;

@Getter
@Setter
public class FuelingCompanyRequest {

    @NotBlank
    private String name;

    private String code;

    private String contactPerson;
    private String phone;
    private String email;
    private String address;
}
