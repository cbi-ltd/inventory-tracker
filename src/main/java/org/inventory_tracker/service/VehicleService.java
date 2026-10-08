package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.VehicleMapper;
import org.inventory_tracker.dto.request.VehicleRequest;
import org.inventory_tracker.dto.response.VehicleResponse;
import org.inventory_tracker.entity.FuelingCompany;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Vehicle;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.FuelingCompanyRepository;
import org.inventory_tracker.repository.VehicleRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final FuelingCompanyRepository fuelingCompanyRepository;
    private final VehicleMapper vehicleMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public VehicleResponse create(VehicleRequest request) {

        Merchant merchant = getCurrentMerchant();

        if (vehicleRepository
                .existsByMerchant_IdAndRegistrationNumberIgnoreCase(
                        merchant.getId(),
                        request.getRegistrationNumber())) {

            throw new DuplicateResourceException(
                    "Vehicle with this registration number already exists"
            );
        }

        FuelingCompany company = fuelingCompanyRepository
                .findByIdAndMerchant_IdAndActiveTrue(
                        request.getCompanyId(),
                        merchant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active fueling company not found"
                        ));

        Vehicle vehicle = vehicleMapper.toEntity(request);

        vehicle.setMerchant(merchant);
        vehicle.setCompany(company);
        vehicle.setActive(true);

        return vehicleMapper.toResponse(
                vehicleRepository.save(vehicle)
        );
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(Long id) {

        return vehicleMapper.toResponse(getVehicle(id));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {

        Merchant merchant = getCurrentMerchant();

        return vehicleMapper.toResponseList(
                vehicleRepository
                        .findByMerchant_IdAndActiveTrueOrderByRegistrationNumberAsc(
                                merchant.getId()
                        )
        );
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getByCompany(Long companyId) {

        Merchant merchant = getCurrentMerchant();

        // Ensure the company belongs to this merchant.
        fuelingCompanyRepository
                .findByIdAndMerchant_IdAndActiveTrue(
                        companyId,
                        merchant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active fueling company not found"
                        ));

        return vehicleMapper.toResponseList(
                vehicleRepository
                        .findByCompany_IdAndMerchant_IdAndActiveTrueOrderByRegistrationNumberAsc(
                                companyId,
                                merchant.getId()
                        )
        );
    }

    public VehicleResponse update(
            Long id,
            VehicleRequest request
    ) {

        Merchant merchant = getCurrentMerchant();

        Vehicle vehicle = vehicleRepository
                .findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle not found"));

        if (vehicleRepository
                .existsByMerchant_IdAndRegistrationNumberIgnoreCaseAndIdNot(
                        merchant.getId(),
                        request.getRegistrationNumber(),
                        id)) {

            throw new DuplicateResourceException(
                    "Vehicle with this registration number already exists"
            );
        }

        FuelingCompany company = fuelingCompanyRepository
                .findByIdAndMerchant_IdAndActiveTrue(
                        request.getCompanyId(),
                        merchant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active fueling company not found"
                        ));

        vehicleMapper.updateEntity(request, vehicle);

        vehicle.setCompany(company);

        return vehicleMapper.toResponse(
                vehicleRepository.save(vehicle)
        );
    }

    public VehicleResponse activate(Long id) {

        Vehicle vehicle = getVehicle(id);

        vehicle.setActive(true);

        return vehicleMapper.toResponse(
                vehicleRepository.save(vehicle)
        );
    }

    public VehicleResponse deactivate(Long id) {

        Vehicle vehicle = getVehicle(id);

        vehicle.setActive(false);

        return vehicleMapper.toResponse(
                vehicleRepository.save(vehicle)
        );
    }

    private Vehicle getVehicle(Long id) {

        Merchant merchant = getCurrentMerchant();

        return vehicleRepository
                .findByIdAndMerchant_Id(id, merchant.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle not found"));
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
