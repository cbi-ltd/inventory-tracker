package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Company;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.config.mapper.StationMapper;
import org.inventory_tracker.repository.CompanyRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final CompanyRepository companyRepository;
    private final StationMapper stationMapper;

    public StationResponse create(CreateStationRequest request) {
        Station station = stationMapper.toEntity(request);

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        station.setCompany(company);
        Station savedStation = stationRepository.save(station);
        return stationMapper.toResponse(savedStation);
    }
}
