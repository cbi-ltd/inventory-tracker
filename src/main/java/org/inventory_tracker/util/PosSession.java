package org.inventory_tracker.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.exception.*;
import org.inventory_tracker.entity.PosSessionResponse;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.enums.Shift;


@Service
@RequiredArgsConstructor
public class PosSession {
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditRepository pumpAuditRepository;
    private final TerminalRepository terminalRepository;


@Transactional(readOnly = true)
public PosSessionResponse getPosSession(Long terminalId, String terminalSerialNumber) {

    Terminal terminal;

    if (terminalId != null) {
        terminal = terminalRepository.findById(terminalId)
                        .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
    } 
    else if (terminalSerialNumber != null) {
        terminal = terminalRepository.findByTerminalSerialNumber(terminalSerialNumber)
                        .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
    } 
    else { throw new BadRequestException("Provide terminalId or terminalSerialNumber."); }

    PumpAssignment latestAssignment;

    if (terminalId != null) {
        latestAssignment = pumpAssignmentRepository
                .findFirstByTerminalIdAndActiveTrueOrderByAssignmentDateDesc(terminal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found for terminal"));

    } 
    else {
        latestAssignment = pumpAssignmentRepository
                .findFirstByTerminal_TerminalSerialNumberAndActiveTrueOrderByAssignmentDateDesc(
                        terminal.getTerminalSerialNumber())
                .orElseThrow(() -> new ResourceNotFoundException("No assignment found for terminal"));
    }

    Station station = latestAssignment.getStation();
    LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
    Shift shift = ShiftUtil.currentShift(station.getTimeZone());

    PumpAssignment assignment;

    if (terminalId != null) {
        assignment = pumpAssignmentRepository
                .findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(terminal.getId(), today, shift)
                .orElseThrow(() -> new ResourceNotFoundException("No active assignment for this terminal"));
    } 
    else {
        assignment = pumpAssignmentRepository
                .findByTerminal_TerminalSerialNumberAndAssignmentDateAndShiftAndActiveTrue(
                        terminal.getTerminalSerialNumber(),
                        today,
                        shift)
                .orElseThrow(() -> new ResourceNotFoundException("No active assignment for this terminal"));
    }

    PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

    Pump pump = assignment.getPump();

    return PosSessionResponse.builder()

            .stationId(station.getId())
            .stationName(station.getName())

            .pumpId(pump.getId())
            .pumpName(pump.getPumpName())
            .pumpNumber(pump.getPumpNumber())

            .productId(pump.getProduct().getId())
            .productName(pump.getProduct().getName())

            .attendantId(assignment.getAttendant().getId())
            .attendantName(assignment.getAttendant().getFullName())

            .terminalId(terminal.getId())
            .tid(terminal.getTid())
            .terminalSerialNumber(
                    terminal.getTerminalSerialNumber())

            .pumpAuditId(audit.getId())
            .openingReading(audit.getOpeningReading())
            .closingReading(audit.getClosingReading())

            .businessDate(today)
            .shift(shift)

            .build();
}
    
    // @Transactional(readOnly = true)
    // public PosSessionResponse getPosSession(Long terminalId, String terminalSerialNumber) {

    //     Terminal terminal = findTerminal(terminalId, terminalSerialNumber);
    //     Station station = terminal.getPumpAssignments() == null ? null : null;
    //     LocalDate today = LocalDate.now();

    //     PumpAssignment assignment =
    //             pumpAssignmentRepository.findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(
    //                             terminal.getId(),
    //                             today,
    //                             ShiftUtil.currentShift()
    //                     )
    //                     .orElseThrow(() -> new ResourceNotFoundException("No active assignment for this terminal"));

    //     Pump pump = assignment.getPump();
    //     station = assignment.getStation();
    //     Product product = pump.getProduct();

    //     Attendant attendant = assignment.getAttendant();
    //     PumpAudit currentAudit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId()).orElse(null);
    //     BigDecimal openingReading = BigDecimal.ZERO;

    //     if (currentAudit != null) {
    //         openingReading = currentAudit.getOpeningReading();
    //     }

    //     BigDecimal previousClosingReading = BigDecimal.ZERO;
    //     PumpAudit previousAudit = pumpAuditRepository
    //                     .findFirstByPumpAssignment_Pump_IdOrderByBusinessDateDescClockInTimeDesc(
    //                             pump.getId()).orElse(null);

    //     if (previousAudit != null) {
    //         previousClosingReading = previousAudit.getClosingReading();
    //     }

    //     return PosSessionResponse.builder()
    //             .stationId(station.getId())
    //             .stationName(station.getName())
    //             .terminalId(terminal.getId())
    //             .terminalSerialNumber(terminal.getTerminalSerialNumber())
    //             .tid(terminal.getTid())
    //             .pumpId(pump.getId())
    //             .pumpNumber(pump.getPumpNumber())
    //             .pumpName(pump.getPumpName())
    //             .productId(product.getId())
    //             .productName(product.getName())
    //             .attendantId(attendant.getId())
    //             .attendantName(attendant.getFullName())
    //             .shift(assignment.getShift())
    //             .businessDate(assignment.getAssignmentDate())
    //             .openingMeterReading(openingReading)
    //             .previousClosingMeterReading(previousClosingReading)
    //             .build();
    // }


    // private Terminal findTerminal(Long terminalId, String terminalSerialNumber) {
    //     if (terminalId != null) {
    //         return terminalRepository.findById(terminalId)
    //                         .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
    //     }

    //     if (terminalSerialNumber != null) {
    //         return terminalRepository.findByTerminalSerialNumber(terminalSerialNumber)
    //                         .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
    //     }

    //     throw new BadRequestException("Either terminalId or terminalSerialNumber must be supplied.");
    // }
}
