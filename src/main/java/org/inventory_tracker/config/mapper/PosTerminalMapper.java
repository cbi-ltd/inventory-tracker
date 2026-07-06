package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreatePosTerminalRequest;
import org.inventory_tracker.dto.response.PosTerminalResponse;
import org.inventory_tracker.entity.PosTerminal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PosTerminalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "active", ignore = true)
    PosTerminal toEntity(CreatePosTerminalRequest request);

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    PosTerminalResponse toResponse(PosTerminal terminal);

    List<PosTerminalResponse> toResponseList(
            List<PosTerminal> terminals
    );
}