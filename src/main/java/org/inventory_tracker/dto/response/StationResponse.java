package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationResponse {

    private Long id;

    private String code;

    private String name;

    private String address;

    private String city;

    private String state;

    private String phoneNumber;

    private String email;

    private Boolean active;
}