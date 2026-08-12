package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantLoginData {

    private String userId;

    private String fullName;

    private String email;

    private String institutionId;

    private String role;
}
