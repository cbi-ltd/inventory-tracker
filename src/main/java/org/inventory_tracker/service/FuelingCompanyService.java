package org.inventory_tracker.service;

import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.config.mapper.FuelingCompanyMapper;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.FuelingCompanyRequest;
import org.inventory_tracker.dto.response.FuelingCompanyResponse;
import org.inventory_tracker.entity.FuelingCompany;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.FuelingCompanyRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class FuelingCompanyService {

    private final FuelingCompanyRepository fuelingCompanyRepository;
    private final FuelingCompanyMapper fuelingCompanyMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public FuelingCompanyResponse create(FuelingCompanyRequest request) {
        Merchant merchant = getCurrentMerchant();

        if (fuelingCompanyRepository.existsByMerchant_IdAndNameIgnoreCase(
                merchant.getId(), request.getName())) {
            throw new DuplicateResourceException("Fueling company with this name already exists");
        }

        if (fuelingCompanyRepository.existsByMerchant_IdAndCodeIgnoreCase(
                merchant.getId(), request.getCode())) {
            throw new DuplicateResourceException("Fueling company with this code already exists");
        }

        FuelingCompany company = fuelingCompanyMapper.toEntity(request);
        company.setMerchant(merchant);
        company.setActive(true);

        return fuelingCompanyMapper.toResponse(
                fuelingCompanyRepository.save(company)
        );
    }

    @Transactional(readOnly = true)
    public FuelingCompanyResponse get(Long id) {
        FuelingCompany company = getCompany(id);
        return fuelingCompanyMapper.toResponse(company);
    }

    @Transactional(readOnly = true)
    public List<FuelingCompanyResponse> getAll() {

        Merchant merchant = getCurrentMerchant();

        return fuelingCompanyMapper.toResponseList(
                fuelingCompanyRepository
                        .findByMerchant_IdAndActiveTrueOrderByNameAsc(
                                merchant.getId()
                        )
        );
    }

    public FuelingCompanyResponse update(
            Long id,
            FuelingCompanyRequest request
    ) {

        Merchant merchant = getCurrentMerchant();

        FuelingCompany company = fuelingCompanyRepository
                .findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fueling company not found"));

        if (fuelingCompanyRepository
                .existsByMerchant_IdAndNameIgnoreCaseAndIdNot(
                        merchant.getId(),
                        request.getName(),
                        id)) {

            throw new DuplicateResourceException(
                    "Fueling company with this name already exists"
            );
        }

        if (fuelingCompanyRepository
                .existsByMerchant_IdAndCodeIgnoreCaseAndIdNot(
                        merchant.getId(),
                        request.getCode(),
                        id)) {

            throw new DuplicateResourceException(
                    "Fueling company with this code already exists"
            );
        }

        fuelingCompanyMapper.updateEntity(request, company);

        return fuelingCompanyMapper.toResponse(
                fuelingCompanyRepository.save(company)
        );
    }

    public FuelingCompanyResponse activate(Long id) {

        FuelingCompany company = getCompany(id);

        company.setActive(true);

        return fuelingCompanyMapper.toResponse(
                fuelingCompanyRepository.save(company)
        );
    }

    public FuelingCompanyResponse deactivate(Long id) {

        FuelingCompany company = getCompany(id);

        company.setActive(false);

        return fuelingCompanyMapper.toResponse(
                fuelingCompanyRepository.save(company)
        );
    }

    private FuelingCompany getCompany(Long id) {

        Merchant merchant = getCurrentMerchant();

        return fuelingCompanyRepository
                .findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fueling company not found"));
    }

    private Merchant getCurrentMerchant() {

        Merchant merchant = authenticatedUserService.getCurrentMerchant();

        if (merchant == null) {
            throw new ResourceNotFoundException(
                    "Merchant is not authenticated"
            );
        }

        return merchant;
    }
}
