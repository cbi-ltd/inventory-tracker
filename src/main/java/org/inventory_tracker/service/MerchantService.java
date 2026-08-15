package org.inventory_tracker.service;

import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.inventory_tracker.dto.response.CamsProfileData;


@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    
    public Merchant getOrCreateMerchant(CamsProfileData profile) {
        return merchantRepository
                .findByCamsMerchantId(profile.getUserId())
                .orElseGet(() -> {
                    Merchant merchant = Merchant.builder()
                            .camsMerchantId(profile.getUserId())
                            .merchantName(profile.getBusinessName())
                            .merchantEmail(profile.getEmail())
                            .merchantRole(profile.getProfileType())
                            .institutionId(profile.getInstitutionId())
                            .build();

                    return merchantRepository.save(merchant);
                });
    }
}
