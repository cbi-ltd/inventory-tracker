package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationResponse {

    private Long id;

    private String name;

    private String address;

    private String email;

    private String phone;

    private Double totalCapacity;

    private Long companyId;

    private String companyName;
}