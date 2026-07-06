package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationRequest {

    @NotBlank
    private String name;

    private String address;

    private String email;

    private String phone;

    private Double totalCapacity;

    private Long companyId;

    private String companyName;
}
