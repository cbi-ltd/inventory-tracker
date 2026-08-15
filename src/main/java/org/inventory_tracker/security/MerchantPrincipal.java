package org.inventory_tracker.security;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class MerchantPrincipal {

    private String merchantId;

    private Long merchantDbId; 

    private String role;

    private String institutionId;

    private String email;

    private String merchantName;
}