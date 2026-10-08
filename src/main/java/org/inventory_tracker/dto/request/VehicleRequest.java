package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequest {

    @NotBlank
    private String registrationNumber;

    @NotNull
    private Long companyId;

    private String make;
    private String model;
    private String description;
}
