package org.inventory_tracker.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public MerchantPrincipal getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new InvalidCamsAuthenticationException(
                    "No authenticated CAMS user found.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof MerchantPrincipal)) {

            throw new InvalidCamsAuthenticationException(
                    "Invalid authenticated principal.");
        }

        return (MerchantPrincipal) principal;
    }

    public String getMerchantId() {

        return getCurrentUser().getMerchantId();
    }

    public String getInstitutionId() {

        return getCurrentUser().getInstitutionId();
    }
}
