package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateAttendantRequest;
import org.inventory_tracker.dto.response.AttendantResponse;
import org.inventory_tracker.entity.Attendant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "active", ignore = true)
    Attendant toEntity(CreateAttendantRequest request);

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    AttendantResponse toResponse(Attendant attendant);

    List<AttendantResponse> toResponseList(List<Attendant> attendants);
}