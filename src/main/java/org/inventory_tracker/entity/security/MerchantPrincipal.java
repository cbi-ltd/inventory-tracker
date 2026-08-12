package org.inventory_tracker.entity.security;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MerchantPrincipal {

    private final String merchantId;

    private final String merchantName;

    private final String email;

    private final String role;

    private final String institutionId;
}
