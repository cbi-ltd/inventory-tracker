package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.response.PumpAssignmentResponse;
import org.inventory_tracker.entity.PumpAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PumpAssignmentMapper {

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")

    @Mapping(source = "pump.id", target = "pumpId")
    @Mapping(source = "pump.name", target = "pumpName")
    @Mapping(source = "pump.pumpNumber", target = "pumpNumber")

    @Mapping(source = "attendant.id", target = "attendantId")
    @Mapping(source = "attendant.fullName", target = "attendantName")
    @Mapping(source = "attendant.username", target = "username")

    PumpAssignmentResponse toResponse(
            PumpAssignment assignment
    );

    List<PumpAssignmentResponse> toResponseList(
            List<PumpAssignment> assignments
    );
}