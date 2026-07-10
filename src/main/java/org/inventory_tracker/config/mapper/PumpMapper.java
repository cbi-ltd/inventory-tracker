package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreatePumpRequest;
import org.inventory_tracker.dto.response.PumpResponse;
import org.inventory_tracker.entity.Pump;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PumpMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "defaultTerminal", ignore = true)
    @Mapping(target = "active", ignore = true)
    Pump toEntity(CreatePumpRequest request);

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "defaultTerminal.id", target = "defaultTerminalDbId")
    @Mapping(source = "defaultTerminal.terminalId", target = "terminalId")
    @Mapping(source = "defaultTerminal.terminalSerialNumber", target = "terminalSerialNumber")
    PumpResponse toResponse(Pump pump);



    List<PumpResponse> toResponseList(List<Pump> pumps);
}
