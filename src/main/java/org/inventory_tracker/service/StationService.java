package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Station;
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.config.mapper.StationMapper;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.inventory_tracker.dto.request.UpdateStationRequest;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final StationMapper stationMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public StationResponse createStation(CreateStationRequest request) {

        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant not found"); }

        // String merchantId = MerchantContext.getCurrentMerchant().getMerchantId();
        // Merchant merchant = merchantRepository.findByCamsMerchantId(merchantId)
        //                         .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        stationRepository.findByCodeIgnoreCase(request.getCode())
                .ifPresent(station -> {
                    throw new DuplicateResourceException(
                            "Station code already exists");
                });

        stationRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(station -> {
                    throw new DuplicateResourceException(
                            "Station name already exists");
                });

                // HAVE TO VERIFY THIS IF IT IS PROVIDED
        // if(request.getMerchantAccountNumber() != merchant.getAccountNumber()){}

        Station station = stationMapper.toEntity(request);
        station.setMerchant(merchant);
        station.setTimeZone(ZoneId.of("Africa/Lagos"));
        Station savedStation = stationRepository.save(station);

        return stationMapper.toResponse(savedStation);
    }

    @Transactional(readOnly = true)
    public StationResponse getStationById(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated");}

        Station station = stationRepository.findByIdAndMerchantId(id, merchant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        // Station station = stationRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        return stationMapper.toResponse(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getAllStations() {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }

        return stationRepository.findAllByMerchantId(merchant.getId()).stream()
                        .map(stationMapper::toResponse).toList();
        // return stationMapper.toResponseList(
        //         stationRepository.findAllByOrderByNameAsc()
        // );
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getActiveStations() {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }
        return stationMapper.toResponseList(stationRepository.findByMerchant_IdAndActiveTrueOrderByNameAsc(merchant.getId()));
    }

    @Transactional
    public StationResponse updateStation(Long id, UpdateStationRequest request) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }

        Station station = stationRepository.findByIdAndMerchantId(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        // Station station = stationRepository.findById(id)
        //                         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        stationMapper.updateStationFromDto(request, station);
        Station updatedStation = stationRepository.save(station);
        return stationMapper.toResponse(updatedStation);
    }

    @Transactional
    public StationResponse activateStation(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }

        Station station = stationRepository.findByIdAndMerchantId(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        // Station station = stationRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        if (Boolean.TRUE.equals(station.getActive())) {
            throw new DuplicateResourceException("Station is already active");
        }

        station.setActive(true);
        Station updatedStation = stationRepository.save(station);
        return stationMapper.toResponse(updatedStation);
    }

    @Transactional
    public StationResponse deactivateStation(Long id) {
        Merchant merchant = authenticatedUserService.getCurrentMerchant();
        if (merchant == null) { throw new ResourceNotFoundException("Merchant is not authenticated"); }

        Station station = stationRepository.findByIdAndMerchantId(id, merchant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        // Station station = stationRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        if (Boolean.FALSE.equals(station.getActive())) {
            throw new DuplicateResourceException("Station is already inactive");
        }

        station.setActive(false);
        Station updatedStation = stationRepository.save(station);
        return stationMapper.toResponse(updatedStation);
    }
}
