package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "attendants", ignore = true)
    @Mapping(target = "terminals", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    Station toEntity(CreateStationRequest request);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    StationResponse toResponse(Station station);
}
