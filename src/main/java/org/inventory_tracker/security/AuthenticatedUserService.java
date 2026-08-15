package org.inventory_tracker.security;


import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.repository.MerchantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {
    private final MerchantRepository merchantRepository;

    public MerchantPrincipal getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCamsAuthenticationException("No authenticated CAMS user found.");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof MerchantPrincipal)) {
            throw new InvalidCamsAuthenticationException("Invalid authenticated principal.");
        }

        return (MerchantPrincipal) principal;
    }

    public Merchant getCurrentMerchant() {
        Long merchantDbId = getMerchantDbId();

        return merchantRepository.findById(merchantDbId)
                .orElseThrow(() ->
                        new InvalidCamsAuthenticationException(
                                "Authenticated merchant not found."));
    }

    public String getMerchantId() {
        return getCurrentUser().getMerchantId();
    }

    public String getInstitutionId() {
        return getCurrentUser().getInstitutionId();
    }

    public Long getMerchantDbId() {
        return getCurrentUser().getMerchantDbId();
    }
}
