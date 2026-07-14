package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.config.mapper.StationMapper;
import org.inventory_tracker.repository.StationRepository;
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

    @Transactional
    public StationResponse createStation(CreateStationRequest request) {

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

        Station station = stationMapper.toEntity(request);
        station.setTimeZone(ZoneId.of("Africa/Lagos"));
        Station savedStation = stationRepository.save(station);

        return stationMapper.toResponse(savedStation);
    }

    @Transactional(readOnly = true)
    public StationResponse getStationById(Long id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Station not found"));

        return stationMapper.toResponse(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getAllStations() {

        return stationMapper.toResponseList(
                stationRepository.findAllByOrderByNameAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getActiveStations() {

        return stationMapper.toResponseList(
                stationRepository.findByActiveTrueOrderByNameAsc()
        );
    }

    @Transactional
    public StationResponse updateStation(Long id, UpdateStationRequest request) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Station not found"));

        stationMapper.updateStationFromDto(request, station);

        // if (!station.getCode().equalsIgnoreCase(request.getCode())) {

        //     stationRepository.findByCodeIgnoreCase(request.getCode())
        //             .ifPresent(existing -> {
        //                 throw new DuplicateResourceException(
        //                         "Station code already exists");
        //             });
        // }

        // if (!station.getName().equalsIgnoreCase(request.getName())) {

        //     stationRepository.findByNameIgnoreCase(request.getName())
        //             .ifPresent(existing -> {
        //                 throw new DuplicateResourceException(
        //                         "Station name already exists");
        //             });
        // }

        // station.setCode(request.getCode());
        // station.setName(request.getName());
        // station.setAddress(request.getAddress());
        // station.setCity(request.getCity());
        // station.setState(request.getState());
        // station.setPhoneNumber(request.getPhoneNumber());
        // station.setEmail(request.getEmail());

        Station updatedStation =
                stationRepository.save(station);

        return stationMapper.toResponse(updatedStation);
    }

    @Transactional
    public StationResponse activateStation(Long id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Station not found"));

        if (Boolean.TRUE.equals(station.getActive())) {
            throw new DuplicateResourceException(
                    "Station is already active");
        }

        station.setActive(true);

        Station updatedStation =
                stationRepository.save(station);

        return stationMapper.toResponse(updatedStation);
    }

    @Transactional
    public StationResponse deactivateStation(Long id) {

        Station station = stationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Station not found"));

        if (Boolean.FALSE.equals(station.getActive())) {
            throw new DuplicateResourceException(
                    "Station is already inactive");
        }

        station.setActive(false);

        Station updatedStation =
                stationRepository.save(station);

        return stationMapper.toResponse(updatedStation);
    }
}
