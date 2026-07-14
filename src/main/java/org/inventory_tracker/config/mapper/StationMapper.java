package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.request.UpdateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.entity.Station;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStationFromDto(UpdateStationRequest dto, @MappingTarget Station entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Station toEntity(CreateStationRequest request);

    StationResponse toResponse(Station station);

    List<StationResponse> toResponseList(
            List<Station> stations);
}
