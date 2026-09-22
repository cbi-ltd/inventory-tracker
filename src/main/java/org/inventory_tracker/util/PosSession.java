package org.inventory_tracker.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.PosAssignmentResponse;
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
import org.inventory_tracker.enums.TerminalMode;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j 
@Service
@RequiredArgsConstructor
public class PosSession {
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditRepository pumpAuditRepository;
    private final TerminalRepository terminalRepository;


 @Transactional(readOnly = true)
     public PosSessionResponse getPosSession(String terminalSerialNumber) {

        Terminal terminal = terminalRepository
                .findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active terminal not found"));

        Station station = terminal.getStation();

        if (station == null) {
                throw new ResourceNotFoundException("Terminal is not associated with a station");
        }

        Merchant merchant = station.getMerchant();

        if (merchant == null) {
                throw new ResourceNotFoundException("Station is not associated with a merchant");
        }

        LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
        Shift currentShift = ShiftUtil.currentShift(station.getTimeZone());
        LocalDate lookupDate = today;
        List<ShiftLookup> lookups;

            switch (currentShift) {

        case MORNING -> lookups = List.of(
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.OVERNIGHT
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.AFTERNOON
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.MORNING
                ),
                new ShiftLookup(
                        today.minusDays(2),
                        Shift.OVERNIGHT
                )
        );

        case AFTERNOON -> lookups = List.of(
                new ShiftLookup(
                        today,
                        Shift.MORNING
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.OVERNIGHT
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.AFTERNOON
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.MORNING
                )
        );

        case OVERNIGHT -> lookups = List.of(
                new ShiftLookup(
                        today,
                        Shift.AFTERNOON
                ),
                new ShiftLookup(
                        today,
                        Shift.MORNING
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.OVERNIGHT
                ),
                new ShiftLookup(
                        today.minusDays(1),
                        Shift.AFTERNOON
                )
        );

        default -> throw new BadRequestException(
                "Unable to determine current shift"
        );
    }

        List<PumpAssignment> assignments = new ArrayList<>();

    LocalDate foundDate = null;
    Shift foundShift = null;

    for (ShiftLookup lookup : lookups) {

        log.info("Looking for previous assignment: terminalId={}, date={}, shift={}",
                terminal.getId(),
                lookup.date(),
                lookup.shift()
        );

        assignments =
                pumpAssignmentRepository
                        .findAllByTerminalIdAndAssignmentDateAndShift(
                                terminal.getId(),
                                lookup.date(),
                                lookup.shift()
                        );

        if (!assignments.isEmpty()) {

            foundDate = lookup.date();
            foundShift = lookup.shift();

            log.info(
                    "Previous assignment found: terminalId={}, date={}, shift={}, count={}",
                    terminal.getId(),
                    foundDate,
                    foundShift,
                    assignments.size()
            );

            break;
        }
    }

        if (station.getTerminalMode() == TerminalMode.SINGLE_PUMP) {
                assignments = List.of(assignments.stream()
                                .max(this::compareAssignmentRecency)
                                .orElseThrow(() ->new ResourceNotFoundException("No previous assignment found for this terminal")));
        }
        else {
                Map<Long, PumpAssignment> latestByAttendant = assignments.stream()
                                .collect(Collectors.toMap( assignment -> assignment.getAttendant().getId(),
                                        assignment -> assignment,
                                        (current, candidate) -> compareAssignmentRecency(candidate, current) > 0 ? candidate : current,
                                        LinkedHashMap::new));

                assignments = new ArrayList<>(latestByAttendant.values());
        }

        List<PosAssignmentResponse> assignmentResponses =
            assignments.stream()
                    .map(assignment -> {

                        if (assignment.getStation() == null
                                || !assignment.getStation().getId()
                                        .equals(station.getId())) {

                            throw new BadRequestException(
                                    "Terminal assignment does not belong to terminal's station");
                        }

                        if (assignment.getTerminal() == null
                                || !assignment.getTerminal().getId()
                                        .equals(terminal.getId())) {

                            throw new BadRequestException(
                                    "Assignment does not belong to this terminal");
                        }

                        Pump pump = assignment.getPump();

                        if (pump == null) {
                            throw new ResourceNotFoundException(
                                    "No pump assigned to this terminal");
                        }

                        Product product = pump.getProduct();

                        if (product == null) {
                            throw new ResourceNotFoundException(
                                    "No product configured for this pump");
                        }

                        PumpAudit audit =
                                pumpAuditRepository
                                        .findByPumpAssignment_Id(assignment.getId())
                                        .orElseThrow(() ->
                                                new ResourceNotFoundException("Pump audit not found"));

                        return PosAssignmentResponse.builder()
                                .pumpId(pump.getId())
                                .pumpNumber(pump.getPumpNumber())
                                .pumpName(pump.getPumpName())
                                .productId(product.getId())
                                .productName(product.getName())
                                .attendantId(assignment.getAttendant().getId())
                                .attendantName(assignment.getAttendant().getFullName())
                                .openingReading(audit.getOpeningReading())
                                .closingReading(audit.getClosingReading())
                                .pumpAuditId(audit.getId())
                                .build();
                    })
                .toList();

                return PosSessionResponse.builder()
                                .stationId(station.getId())
                                .stationName(station.getName())
                                .terminalId(terminal.getId())
                                .terminalSerialNumber(terminal.getTerminalSerialNumber())
                                .tid(terminal.getTid())
                                .shift(foundShift)
                                .businessDate(lookupDate)
                                .terminalMode(station.getTerminalMode())
                                .assignments(assignmentResponses)
                                .build();
    }



//      @Transactional(readOnly = true)
//      public PosSessionResponse getPosSession(String terminalSerialNumber) {

//         Terminal terminal = terminalRepository
//                 .findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
//                 .orElseThrow(() ->
//                         new ResourceNotFoundException(
//                                 "Active terminal not found"));

//         Station station = terminal.getStation();

//         if (station == null) {
//                 throw new ResourceNotFoundException("Terminal is not associated with a station");
//         }

//         Merchant merchant = station.getMerchant();

//         if (merchant == null) {
//                 throw new ResourceNotFoundException("Station is not associated with a merchant");
//         }

//         LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
//         Shift currentShift = ShiftUtil.currentShift(station.getTimeZone());
//         LocalDate lookupDate = today;
//         Shift lookupShift = currentShift;
//         List<PumpAssignment> assignments = new ArrayList<>();

//         int attempts = 0;
//         int maxAttempts = 100;

//         switch (currentShift) {
//                 case MORNING -> {
//                         lookupShift = Shift.OVERNIGHT;
//                         lookupDate = lookupDate.minusDays(1);
//                 }
//                 case AFTERNOON -> { lookupShift = Shift.MORNING; }
//                 case OVERNIGHT -> { lookupShift = Shift.AFTERNOON; }
//                 default -> throw new BadRequestException("Unable to determine previous shift");
//         }

//         while (assignments.isEmpty() && attempts < maxAttempts) {
//                 assignments = pumpAssignmentRepository
//                         .findAllByTerminalIdAndAssignmentDateAndShift(terminal.getId(), lookupDate, lookupShift);

//                 if (!assignments.isEmpty()) { break; }

//                 switch (lookupShift) {
//                         case MORNING -> {
//                                 lookupShift = Shift.OVERNIGHT;
//                                 lookupDate = lookupDate.minusDays(1);
//                         }

//                         case AFTERNOON -> {
//                                 lookupShift = Shift.MORNING;
//                         }

//                         case OVERNIGHT -> {
//                                 lookupShift = Shift.AFTERNOON;
//                         }

//                         default -> throw new BadRequestException("Unable to determine previous shift");
//                 }

//                 attempts++;
//         }
//         if (assignments.isEmpty()) {
//                 throw new ResourceNotFoundException("No previous assignment for this terminal");
//         }

//         if (station.getTerminalMode() == TerminalMode.SINGLE_PUMP) {
//                 assignments = List.of(assignments.stream()
//                                 .max(this::compareAssignmentRecency)
//                                 .orElseThrow(() ->new ResourceNotFoundException("No previous assignment found for this terminal")));
//         }
//         else {
//                 Map<Long, PumpAssignment> latestByAttendant = assignments.stream()
//                                 .collect(Collectors.toMap( assignment -> assignment.getAttendant().getId(),
//                                         assignment -> assignment,
//                                         (current, candidate) -> compareAssignmentRecency(candidate, current) > 0 ? candidate : current,
//                                         LinkedHashMap::new));

//                 assignments = new ArrayList<>(latestByAttendant.values());
//         }

//         List<PosAssignmentResponse> assignmentResponses =
//             assignments.stream()
//                     .map(assignment -> {

//                         if (assignment.getStation() == null
//                                 || !assignment.getStation().getId()
//                                         .equals(station.getId())) {

//                             throw new BadRequestException(
//                                     "Terminal assignment does not belong to terminal's station");
//                         }

//                         if (assignment.getTerminal() == null
//                                 || !assignment.getTerminal().getId()
//                                         .equals(terminal.getId())) {

//                             throw new BadRequestException(
//                                     "Assignment does not belong to this terminal");
//                         }

//                         Pump pump = assignment.getPump();

//                         if (pump == null) {
//                             throw new ResourceNotFoundException(
//                                     "No pump assigned to this terminal");
//                         }

//                         Product product = pump.getProduct();

//                         if (product == null) {
//                             throw new ResourceNotFoundException(
//                                     "No product configured for this pump");
//                         }

//                         PumpAudit audit =
//                                 pumpAuditRepository
//                                         .findByPumpAssignment_Id(assignment.getId())
//                                         .orElseThrow(() ->
//                                                 new ResourceNotFoundException("Pump audit not found"));

//                         return PosAssignmentResponse.builder()
//                                 .pumpId(pump.getId())
//                                 .pumpNumber(pump.getPumpNumber())
//                                 .pumpName(pump.getPumpName())
//                                 .productId(product.getId())
//                                 .productName(product.getName())
//                                 .attendantId(assignment.getAttendant().getId())
//                                 .attendantName(assignment.getAttendant().getFullName())
//                                 .openingReading(audit.getOpeningReading())
//                                 .closingReading(audit.getClosingReading())
//                                 .pumpAuditId(audit.getId())
//                                 .build();
//                     })
//                 .toList();

//                 return PosSessionResponse.builder()
//                                 .stationId(station.getId())
//                                 .stationName(station.getName())
//                                 .terminalId(terminal.getId())
//                                 .terminalSerialNumber(terminal.getTerminalSerialNumber())
//                                 .tid(terminal.getTid())
//                                 .shift(lookupShift)
//                                 .businessDate(lookupDate)
//                                 .terminalMode(station.getTerminalMode())
//                                 .assignments(assignmentResponses)
//                                 .build();
//     }


// @Transactional(readOnly = true)
// public PosSessionResponse getPosSession(String terminalSerialNumber) {

//     Terminal terminal = terminalRepository
//             .findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
//             .orElseThrow(() ->
//                     new ResourceNotFoundException(
//                             "Active terminal not found"));

//     Station station = terminal.getStation();

//     if (station == null) {
//         throw new ResourceNotFoundException(
//                 "Terminal is not associated with a station");
//     }

//     Merchant merchant = station.getMerchant();

//     if (merchant == null) {
//         throw new ResourceNotFoundException(
//                 "Station is not associated with a merchant");
//     }

//     LocalDate businessDate =
//             ShiftUtil.businessDate(station.getTimeZone());

//     Shift currentShift =
//             ShiftUtil.currentShift(station.getTimeZone());

//     Shift previousShift;

//     switch (currentShift) {
//         case MORNING -> previousShift = Shift.OVERNIGHT;
//         case AFTERNOON -> previousShift = Shift.MORNING;
//         case OVERNIGHT -> previousShift = Shift.AFTERNOON;
//         default -> throw new BadRequestException(
//                 "Unable to determine previous shift");
//     }

//     List<PumpAssignment> assignments =
//             pumpAssignmentRepository
//                     .findAllByTerminalIdAndAssignmentDateAndShift(
//                             terminal.getId(),
//                             businessDate,
//                             previousShift
//                     );

//     if (assignments.isEmpty()) {
//         throw new ResourceNotFoundException(
//                 "No previous shift assignment found for this terminal");
//     }

//     if (station.getTerminalMode() == TerminalMode.SINGLE_PUMP) {

//         assignments = List.of(
//                 assignments.stream()
//                         .max(this::compareAssignmentRecency)
//                         .orElseThrow(() ->
//                                 new ResourceNotFoundException(
//                                         "No previous assignment found for this terminal"))
//         );

//     } else {

//         Map<Long, PumpAssignment> latestByAttendant =
//                 assignments.stream()
//                         .collect(Collectors.toMap(
//                                 assignment ->
//                                         assignment.getAttendant().getId(),
//                                 assignment -> assignment,
//                                 (current, candidate) ->
//                                         compareAssignmentRecency(
//                                                 candidate,
//                                                 current) > 0
//                                                 ? candidate
//                                                 : current,
//                                 LinkedHashMap::new
//                         ));

//         assignments = new ArrayList<>(latestByAttendant.values());
//     }

//     List<PosAssignmentResponse> assignmentResponses =
//             assignments.stream()
//                     .map(assignment -> {

//                         if (assignment.getStation() == null
//                                 || !assignment.getStation().getId()
//                                         .equals(station.getId())) {

//                             throw new BadRequestException(
//                                     "Terminal assignment does not belong to terminal's station");
//                         }

//                         if (assignment.getTerminal() == null
//                                 || !assignment.getTerminal().getId()
//                                         .equals(terminal.getId())) {

//                             throw new BadRequestException(
//                                     "Assignment does not belong to this terminal");
//                         }

//                         Pump pump = assignment.getPump();

//                         if (pump == null) {
//                             throw new ResourceNotFoundException(
//                                     "No pump assigned to this terminal");
//                         }

//                         Product product = pump.getProduct();

//                         if (product == null) {
//                             throw new ResourceNotFoundException(
//                                     "No product configured for this pump");
//                         }

//                         PumpAudit audit =
//                                 pumpAuditRepository
//                                         .findByPumpAssignment_Id(
//                                                 assignment.getId())
//                                         .orElseThrow(() ->
//                                                 new ResourceNotFoundException(
//                                                         "Pump audit not found"));

//                         return PosAssignmentResponse.builder()
//                                 .pumpId(pump.getId())
//                                 .pumpNumber(pump.getPumpNumber())
//                                 .pumpName(pump.getPumpName())
//                                 .productId(product.getId())
//                                 .productName(product.getName())
//                                 .attendantId(
//                                         assignment.getAttendant().getId())
//                                 .attendantName(
//                                         assignment.getAttendant()
//                                                 .getFullName())
//                                 .openingReading(
//                                         audit.getOpeningReading())
//                                 .closingReading(
//                                         audit.getClosingReading())
//                                 .pumpAuditId(audit.getId())
//                                 .build();
//                     })
//                     .toList();

//     return PosSessionResponse.builder()
//             .stationId(station.getId())
//             .stationName(station.getName())
//             .terminalId(terminal.getId())
//             .terminalSerialNumber(
//                     terminal.getTerminalSerialNumber())
//             .tid(terminal.getTid())
//             .shift(previousShift)
//             .businessDate(businessDate)
//             .terminalMode(station.getTerminalMode())
//             .assignments(assignmentResponses)
//             .build();
// }

    private int compareAssignmentRecency(PumpAssignment first, PumpAssignment second) {
        int dateComparison = first.getAssignmentDate().compareTo(second.getAssignmentDate());
        if (dateComparison != 0) { return dateComparison; }

        return Integer.compare(first.getShift().getOrder(), second.getShift().getOrder());
    }

    private record ShiftLookup(LocalDate date, Shift shift) {}




    private void newVerifyAssignmentOwnership(PumpAssignment assignment, String camsMerchantId) {
        if (assignment == null
                || assignment.getTerminal() == null
                || assignment.getTerminal().getStation() == null
                || assignment.getTerminal().getStation().getMerchant() == null) {

                throw new ResourceNotFoundException("Assignment ownership could not be verified");
        }

        String assignmentMerchantId = assignment.getTerminal().getStation().getMerchant().getCamsMerchantId();

        if (!Objects.equals(assignmentMerchantId, camsMerchantId)) {
                throw new ResourceNotFoundException("Assignment not found");
        }
    }
}
