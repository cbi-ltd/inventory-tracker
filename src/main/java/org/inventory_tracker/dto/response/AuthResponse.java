package org.inventory_tracker.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {

    private String token;

    private String role;

    private String email;

    private String fullName;
}
