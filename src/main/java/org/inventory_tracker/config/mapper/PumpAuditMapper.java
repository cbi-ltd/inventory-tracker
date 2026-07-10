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
    @Mapping(target = "pumpAssignment", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "openingReading", ignore = true)
    @Mapping(target = "closingReading", ignore = true)
    @Mapping(target = "totalDispensed", ignore = true)
    @Mapping(target = "clockInTime", ignore = true)
    @Mapping(target = "clockOutTime", ignore = true)
    PumpAudit toEntity(CreatePumpAuditRequest request);

    @Mapping(source = "pumpAssignment.id", target = "pumpAssignmentId")

    @Mapping(source = "pumpAssignment.pump.id", target = "pumpId")
    @Mapping(source = "pumpAssignment.pump.pumpNumber", target = "pumpNumber")

    @Mapping(source = "pumpAssignment.attendant.id", target = "attendantId")
    @Mapping(source = "pumpAssignment.attendant.fullName", target = "attendantName")

    @Mapping(source = "pumpAssignment.station.id", target = "stationId")
    @Mapping(source = "pumpAssignment.station.name", target = "stationName")

    @Mapping(source = "pumpAssignment.assignmentDate", target = "assignmentDate")
    @Mapping(source = "pumpAssignment.shift", target = "shift")

    @Mapping(source="pumpAssignment.terminal.id", target="terminalDbId")
    @Mapping(source="pumpAssignment.terminal.terminalId", target="terminalId")
    @Mapping(source="pumpAssignment.terminal.terminalSerialNumber", target="terminalSerialNumber")

    // @Mapping(source = "pumpAssignment.actualTerminalId", target = "terminalId")
    // @Mapping(source = "pumpAssignment.actualTerminalSerialNumber", target = "terminalSerialNumber")

    @Mapping(source = "businessDate", target = "businessDate")

    PumpAuditResponse toResponse(PumpAudit audit);

    List<PumpAuditResponse> toResponseList(List<PumpAudit> audits);
}