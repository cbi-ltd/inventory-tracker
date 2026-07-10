package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Station toEntity(CreateStationRequest request);

    StationResponse toResponse(Station station);

    List<StationResponse> toResponseList(
            List<Station> stations);
}
