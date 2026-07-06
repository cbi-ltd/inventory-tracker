package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.inventory_tracker.entity.PumpAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PumpAuditMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pump", ignore = true)
    PumpAudit toEntity(
            CreatePumpAuditRequest request
    );

    @Mapping(
            target = "totalDispensed",
            expression =
                    "java(audit.getClosingReading() - audit.getOpeningReading())"
    )
    PumpAuditResponse toResponse(
            PumpAudit audit
    );

    List<PumpAuditResponse> toResponseList(
            List<PumpAudit> audits
    );




//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "station", ignore = true)
//    @Mapping(target = "attendant", ignore = true)
//    @Mapping(target = "posTerminal", ignore = true)
//    PumpAudit toEntity(CreatePumpAuditRequest request);
//
//    @Mapping(source = "station.id", target = "stationId")
//    @Mapping(source = "station.name", target = "stationName")
//    @Mapping(source = "attendant.id", target = "attendantId")
//    @Mapping(source = "attendant.fullName", target = "attendantName")
//    @Mapping(source = "posTerminal.id", target = "posTerminalId")
//    @Mapping(
//            source = "posTerminal.serialNumber",
//            target = "terminalSerialNumber"
//    )
//    @Mapping(
//            target = "totalDispensed",
//            expression =
//                    "java(audit.getClosingReading() - audit.getOpeningReading())"
//    )
//    PumpAuditResponse toResponse(PumpAudit audit);
//
//    List<PumpAuditResponse> toResponseList(
//            List<PumpAudit> audits
//    );
}