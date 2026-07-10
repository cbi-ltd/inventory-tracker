package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateStationRequest {

    @NotBlank(message = "Station code is required")
    private String code;

    @NotBlank(message = "Station name is required")
    private String name;

    @NotBlank(message = "Station address is required")
    private String address;

    private String city;

    private String state;

    private String phoneNumber;

    private String email;
}
