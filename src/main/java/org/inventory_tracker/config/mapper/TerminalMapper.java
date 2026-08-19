package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.TerminalSyncRequest;
import org.inventory_tracker.dto.response.TerminalResponse;
import org.inventory_tracker.entity.Terminal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TerminalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "lastSyncedAt", ignore = true)
    Terminal toEntity(TerminalSyncRequest request);

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    TerminalResponse toResponse(Terminal terminal);

    List<TerminalResponse> toResponseList(
            List<Terminal> terminals);
}