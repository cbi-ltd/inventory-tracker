package org.inventory_tracker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private Boolean franchise;

    private String operatorName;
}
