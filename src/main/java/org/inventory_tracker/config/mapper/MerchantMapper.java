package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.MerchantLoginData;
import org.inventory_tracker.dto.request.MerchantTerminalPrepData;
import org.inventory_tracker.entity.Merchant;
import org.springframework.stereotype.Component;


@Component
public class MerchantMapper {

    public Merchant fromLogin(MerchantLoginData dto) {

        return Merchant.builder()
                .camsMerchantId(dto.getUserId())
                // .merchantName(dto.getFullName())
                // .merchantEmail(dto.getEmail())
                .merchantRole(dto.getRole())
                .institutionId(dto.getInstitutionId())
                .build();
    }

    public Merchant fromPrep(MerchantTerminalPrepData dto) {

        return Merchant.builder()
                .camsMerchantId(dto.getUserProfileId())
                // .merchantName(dto.getMerchantName())
                // .merchantEmail(dto.getMerchantEmail())
                .merchantRole(dto.getMerchantType())
                .institutionId(dto.getInstitutionId())
                .build();
    }
}
