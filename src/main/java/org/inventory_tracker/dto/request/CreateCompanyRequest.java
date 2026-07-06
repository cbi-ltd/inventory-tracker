package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompanyRequest {

    @NotBlank
    private String name;

    private String address;

    @Email
    private String email;

    private String phone;

    private Boolean franchise;

    private String operatorName;
}
