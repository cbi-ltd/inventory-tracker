package org.inventory_tracker.service;

import org.inventory_tracker.dto.request.MerchantLoginData;
import org.inventory_tracker.dto.request.MerchantTerminalPrepData; 
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.repository.MerchantRepository;
import org.inventory_tracker.config.mapper.MerchantMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository repository;
    private final MerchantMapper mapper;

    public Merchant findFromLogin(MerchantLoginData loginData){
        Merchant merchant = repository.findByCamsMerchantId(loginData.getUserId())
                                .orElseGet(() -> mapper.fromLogin(loginData));

        if (merchant.getMerchantName() == null || merchant.getMerchantName().isBlank()) {
            merchant.setMerchantName("Unknown");
        }
        else{
            merchant.setMerchantName(loginData.getFullName());
        }
        if (merchant.getMerchantEmail() == null || merchant.getMerchantEmail().isBlank()) {
            merchant.setMerchantEmail("Unknown");
        }
        else{
            merchant.setMerchantEmail(loginData.getEmail());
        }

        return repository.save(merchant);
    }

    public Merchant findFromTerminalPrep(MerchantTerminalPrepData prepData){
        Merchant merchant = repository.findByCamsMerchantId(prepData.getUserProfileId())
                                .orElseGet(() -> mapper.fromPrep(prepData));

        if (merchant.getMerchantName() == null || merchant.getMerchantName().isBlank()) {
            merchant.setMerchantName("Unknown");
        }
        else{
            merchant.setMerchantName(prepData.getMerchantName());
        }
        if (merchant.getMerchantEmail() == null || merchant.getMerchantEmail().isBlank()) {
            merchant.setMerchantEmail("Unknown");
        }
        else{
            merchant.setMerchantEmail(prepData.getMerchantEmail());
        }


        return repository.save(merchant);
    }

    public Merchant getCurrentMerchant(String camsMerchantId){
        return repository.findByCamsMerchantId(camsMerchantId).orElse(null);
    }
}
