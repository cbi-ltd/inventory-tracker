package org.inventory_tracker.service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.dto.request.AssignPumpRequest;
import org.inventory_tracker.dto.request.ChangeTerminalAssignmentRequest;
import org.inventory_tracker.dto.response.PumpAssignmentResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.enums.TerminalMode;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.util.ShiftUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAssignmentMapper;
import java.util.List;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.security.MerchantPrincipal;


@Service
@RequiredArgsConstructor
public class PumpAssignmentService {
    private final AttendantRepository attendantRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpRepository pumpRepository;
    private final PumpAssignmentMapper pumpAssignmentMapper;
    private final StationRepository stationRepository;
    private final TerminalRepository terminalRepository;
    private final AuthenticatedUserService authenticatedUserService;


//     @Transactional
//     public PumpAssignmentResponse changeTerminalAssignment(Long assignmentId,
//         ChangeTerminalAssignmentRequest request) {
//         MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
//         PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
//                         .orElseThrow(() ->
//                                 new ResourceNotFoundException(
//                                         "Pump assignment not found"));
//         if (!assignment.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
//                 throw new ResourceNotFoundException("Pump assignment not found");
//         }

//         Pump pump = assignment.getPump();
//         Station station = assignment.getStation();
//         Terminal terminal = terminalRepository.findById(request.getTerminalId())
//                                 .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

//         boolean terminalBelongsToPump = (pump.getDefaultTerminal() != null
//                                                 && pump.getDefaultTerminal().getId().equals(terminal.getId())) ||
//                                         (pump.getTerminalSerialNumber() != null
//                                                 && pump.getTerminalSerialNumber().equals(terminal.getTerminalSerialNumber()));

//         if (!terminalBelongsToPump) {
//                 throw new ResourceNotFoundException("Terminal not found");
//         }

//         assignment.setTerminal(terminal);
//         PumpAssignment updated = pumpAssignmentRepository.save(assignment);

//         return pumpAssignmentMapper.toResponse(updated);
//     }


        @Transactional
        public PumpAssignmentResponse changeTerminalAssignment(Long assignmentId, ChangeTerminalAssignmentRequest request) {
                MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
                PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));

                if (!assignment.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                        throw new ResourceNotFoundException("Pump assignment not found");
                }

                Station station = assignment.getStation();

                    if (station == null ||
            station.getMerchant() == null ||
            !station.getMerchant().getId()
                    .equals(principal.getMerchantDbId())) {

        throw new ResourceNotFoundException(
                "Pump assignment not found");
    }

    if (!Boolean.TRUE.equals(assignment.getActive())) {
        throw new BadRequestException(
                "Cannot change a closed pump assignment");
    }

    if (request.getTerminalId() == null) {
        throw new BadRequestException(
                "Terminal ID is required");
    }

                Pump pump = assignment.getPump();

                Terminal terminal = terminalRepository.findById(request.getTerminalId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

                if (terminal.getStation() == null || !terminal.getStation().getId().equals(station.getId())) {
                        throw new BadRequestException("Terminal does not belong to this station");
                }

                if (!Boolean.TRUE.equals(terminal.getActive())) {
        throw new BadRequestException(
                "Terminal is inactive");
    }


                if (station.getTerminalMode() == TerminalMode.SINGLE_PUMP) {
                        boolean terminalBelongsToPump = (pump.getDefaultTerminal() != null && pump.getDefaultTerminal()
                                                .getId().equals(terminal.getId()))
                                ||
                                (pump.getTerminalSerialNumber() != null
                                        && pump.getTerminalSerialNumber().equals(terminal.getTerminalSerialNumber()));

                        if (!terminalBelongsToPump) {
                                throw new BadRequestException("Terminal is not configured for this pump");
                        }
                }

                assignment.setTerminal(terminal);
                PumpAssignment updated = pumpAssignmentRepository.save(assignment);
                return pumpAssignmentMapper.toResponse(updated);
        }

        
//     @Transactional
//     public PumpAssignmentResponse assignPumpToAttendant(AssignPumpRequest request) {
//         MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        
//         Station station = stationRepository.findById(request.getStationId())
//                                 .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

//         if (!station.getMerchant().getId().equals(principal.getMerchantDbId())) {
//                 throw new ResourceNotFoundException("Station not found");
//         }
        
//         Attendant attendant = attendantRepository.findById(request.getAttendantId())
//                 .orElseThrow(() -> new ResourceNotFoundException("Attendant not found"));

//         if (!attendant.getStation().getId().equals(station.getId())) {
//                 throw new BadRequestException("Attendant does not belong to this station");
//         }


//         Pump pump = pumpRepository.findById(request.getPumpId())
//                         .orElseThrow(() ->new ResourceNotFoundException("Pump not found"));

//         if (!pump.getStation().getId().equals(station.getId())) {
//                 throw new BadRequestException("Pump does not belong to this station");
//         }
                        
//         LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
//         Shift currentShift = ShiftUtil.currentShift(station.getTimeZone());

//         pumpAssignmentRepository
//                 .findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(pump.getId(), today,currentShift)
//                 .ifPresent(existing -> {
//                     throw new DuplicateResourceException(
//                             "Pump " + pump.getPumpNumber()
//                                     + " is already assigned for the current shift."
//                     );
//                 });

//         List<PumpAssignment> activeAssignments = pumpAssignmentRepository
//                                                         .findAllByAttendantIdAndActiveTrue(attendant.getId());

//         if (!activeAssignments.isEmpty()) {
//             activeAssignments.forEach(a -> a.setActive(false));
//             pumpAssignmentRepository.saveAll(activeAssignments);
//         }

//         Terminal terminal = (pump.getDefaultTerminal() != null) ? pump.getDefaultTerminal() : terminalRepository.findByTerminalSerialNumber(pump.getTerminalSerialNumber())
//                                 .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

//         boolean terminalBelongsToPump = (pump.getDefaultTerminal() != null
//                                                 && pump.getDefaultTerminal().getId().equals(terminal.getId())) ||
//                                         (pump.getTerminalSerialNumber() != null
//                                                 && pump.getTerminalSerialNumber().equals(terminal.getTerminalSerialNumber()));

//         if (!terminalBelongsToPump) {
//                 throw new ResourceNotFoundException("Terminal not found");
//         }
                         
//         PumpAssignment assignment = new PumpAssignment();
//         assignment.setPump(pump);
//         assignment.setTerminal(terminal);
//         assignment.setAttendant(attendant);
//         assignment.setStation(station);
//         assignment.setAssignmentDate(today);
//         assignment.setShift(currentShift);
//         assignment.setActive(true);

//         PumpAssignment saved = pumpAssignmentRepository.save(assignment);
//         return pumpAssignmentMapper.toResponse(saved);
//     }


    @Transactional
    public PumpAssignmentResponse assignPumpToAttendant(AssignPumpRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        
        Station station = stationRepository.findById(request.getStationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        if (!station.getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Station not found");
        }
        
        Attendant attendant = attendantRepository.findById(request.getAttendantId())
                .orElseThrow(() -> new ResourceNotFoundException("Attendant not found"));

        if (!attendant.getStation().getId().equals(station.getId())) {
                throw new BadRequestException("Attendant does not belong to this station");
        }


        Pump pump = pumpRepository.findById(request.getPumpId())
                        .orElseThrow(() ->new ResourceNotFoundException("Pump not found"));

        if (!pump.getStation().getId().equals(station.getId())) {
                throw new BadRequestException("Pump does not belong to this station");
        }
                        
        LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
        Shift currentShift = ShiftUtil.currentShift(station.getTimeZone());

        pumpAssignmentRepository
                .findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(pump.getId(), today,currentShift)
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Pump " + pump.getPumpNumber()
                                    + " is already assigned for the current shift."
                    );
                });

        List<PumpAssignment> activeAssignments = pumpAssignmentRepository
                                                        .findAllByAttendantIdAndActiveTrue(attendant.getId());

        if (!activeAssignments.isEmpty()) {
            activeAssignments.forEach(a -> a.setActive(false));
            pumpAssignmentRepository.saveAll(activeAssignments);
        }

        Terminal terminal;

        if (station.getTerminalMode() == TerminalMode.MULTI_PUMP) {

        // Multi-pump mode requires an explicitly selected terminal.
        if (request.getTerminalId() == null) {
            throw new BadRequestException(
                    "Terminal ID is required for multi-pump stations");
        }

        terminal = terminalRepository.findById(request.getTerminalId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Terminal not found"));

    }else{

        if (pump.getDefaultTerminal() != null) {
                terminal = pump.getDefaultTerminal();
        } 
        else if (pump.getTerminalSerialNumber() != null) {

        terminal = terminalRepository
                .findByTerminalSerialNumber(pump.getTerminalSerialNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
        } 
        else {
                throw new ResourceNotFoundException("No terminal configured for pump");
        }
}

        if (terminal.getStation() == null || !terminal.getStation().getId().equals(station.getId())) {
                throw new BadRequestException("Terminal does not belong to this station");
        }

// if (station.getTerminalMode() == TerminalMode.SINGLE_PUMP) {

//     boolean terminalBelongsToPump =
//             (pump.getDefaultTerminal() != null
//                     && pump.getDefaultTerminal()
//                             .getId()
//                             .equals(terminal.getId()))
//             ||
//             (pump.getTerminalSerialNumber() != null
//                     && pump.getTerminalSerialNumber()
//                             .equals(terminal.getTerminalSerialNumber()));

//     if (!terminalBelongsToPump) {
//         throw new ResourceNotFoundException("Terminal not found");
//     }
// }

    if (!Boolean.TRUE.equals(terminal.getActive())) {
        throw new BadRequestException(
                "Terminal is inactive");
    }
          
        PumpAssignment assignment = new PumpAssignment();
        assignment.setPump(pump);
        assignment.setTerminal(terminal);
        assignment.setAttendant(attendant);
        assignment.setStation(station);
        assignment.setAssignmentDate(today);
        assignment.setShift(currentShift);
        assignment.setActive(true);

        PumpAssignment saved = pumpAssignmentRepository.save(assignment);
        return pumpAssignmentMapper.toResponse(saved);
    }


    @Transactional(readOnly = true)
    public PumpAssignmentResponse getPumpCurrentAssignment(Long attendantId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        PumpAssignment assignment = pumpAssignmentRepository.findByAttendantIdAndActiveTrue(attendantId)
                                        .orElseThrow(() -> new ResourceNotFoundException("No active pump assignment found."));

        if (!assignment.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("No active pump assignment found.");
        }
                                
        return pumpAssignmentMapper.toResponse(assignment);
     }

     @Transactional(readOnly = true)
     public List<PumpAssignmentResponse> getAttendantPumpAssignmentHistory(Long attendantId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Attendant attendant = attendantRepository.findById(attendantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendant not found"));
        
        if (!attendant.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Attendant not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                        .findByAttendantIdOrderByAssignmentDateDesc(attendantId));
     }

    @Transactional(readOnly = true)
    public List<PumpAssignmentResponse> getTodayPumpAssignments(Long stationId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        
        if (!station.getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Station not found");
        }

        LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                        .findByStationIdAndAssignmentDateOrderByPump_PumpNumberAsc(stationId,today));
    }

    @Transactional(readOnly = true)
    public List<PumpAssignmentResponse> getAllAssignments() {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                    .findByStation_Merchant_IdOrderByAssignmentDateDescShiftAsc(principal.getMerchantDbId()));
    }

    @Transactional(readOnly = true)
    public PumpAssignmentResponse getAssignmentById(Long assignmentId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));
        
        if (!assignment.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Pump assignment not found");
        }

        return pumpAssignmentMapper.toResponse(assignment);
    }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByStation(Long stationId) {
       MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Station station = stationRepository.findById(stationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
      
        if (!station.getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Station not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                     .findByStationIdOrderByAssignmentDateDescShiftAsc(stationId));
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByPump(Long pumpId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Pump pump = pumpRepository.findById(pumpId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
                
        if (!pump.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Pump not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                        .findByPumpIdOrderByAssignmentDateDescShiftAsc(pumpId));
   }

   @Transactional(readOnly = true)
   public PumpAssignmentResponse getCurrentPumpAssignment(Long pumpId) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        Pump pump = pumpRepository.findById(pumpId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
                
        if (!pump.getStation().getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Pump not found");
        }

        PumpAssignment assignment = pumpAssignmentRepository.findByPumpIdAndActiveTrue(pumpId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump has no active assignment."));

        return pumpAssignmentMapper.toResponse(assignment);
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByDate(LocalDate assignmentDate) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                .findByStation_Merchant_IdAndAssignmentDateOrderByStation_NameAscPump_PumpNumberAsc(principal.getMerchantDbId(), assignmentDate));
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByShift(Shift shift) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();

                return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                .findByStation_Merchant_IdAndShiftOrderByAssignmentDateDescStation_NameAscPump_PumpNumberAsc(principal.getMerchantDbId(), shift));
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getCurrentAssignmentsByTerminal(String terminalSerialNumber) {

            if (terminalSerialNumber == null || terminalSerialNumber.isBlank()) {
        throw new BadRequestException(
                "Terminal serial number is required"
        );
    }

        Terminal terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                        .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        Station station = terminal.getStation();

        if (station == null) {
        throw new ResourceNotFoundException(
                "Terminal is not associated with a station"
        );
    }

        if (station.getTerminalMode() != TerminalMode.MULTI_PUMP) {
                throw new BadRequestException("This endpoint is only available for MULTI_PUMP stations");
        }

        LocalDate businessDate = ShiftUtil.businessDate(station.getTimeZone());
        Shift shift = ShiftUtil.currentShift(station.getTimeZone());

        List<PumpAssignment> assignments = pumpAssignmentRepository
                        .findByTerminal_IdAndAssignmentDateAndShiftAndActiveTrue(
                                terminal.getId(), businessDate, shift);

        if (assignments.isEmpty()) {
                throw new ResourceNotFoundException("No active pump assignments found for this terminal");
        }

        for (PumpAssignment assignment : assignments) {
                if (assignment.getTerminal() == null) {
                        throw new ResourceNotFoundException("Pump assignment " + assignment.getId() + " is not associated with a terminal"
                );
                }

                if (!assignment.getTerminal()
                        .getId()
                        .equals(terminal.getId())) {

                throw new BadRequestException(
                        "Pump assignment "
                                + assignment.getId()
                                + " does not belong to this terminal"
                );
                }


                if (assignment.getStation() == null) {
                throw new ResourceNotFoundException(
                        "Pump assignment "
                                + assignment.getId()
                                + " is not associated with a station"
                );
                }

                if (!assignment.getStation()
                        .getId()
                        .equals(station.getId())) {

                throw new BadRequestException(
                        "Pump assignment "
                                + assignment.getId()
                                + " does not belong to terminal's station"
                );
                }


                /*
                * Optional but useful validation:
                * make sure the assignment has a pump.
                */

                if (assignment.getPump() == null) {
                throw new ResourceNotFoundException(
                        "Pump assignment "
                                + assignment.getId()
                                + " is not associated with a pump"
                );
                }


                /*
                * Make sure the assignment has an attendant.
                */

                if (assignment.getAttendant() == null) {
                throw new ResourceNotFoundException(
                        "Pump assignment "
                                + assignment.getId()
                                + " is not associated with an attendant"
                );
                }
        }

        return pumpAssignmentMapper.toResponseList(assignments);
 }

//    private Merchant getAuthenticatedMerchant() {
//         Merchant merchant = MerchantContext.getCurrentMerchant();
//         if (merchant == null) {
//                 throw new ResourceNotFoundException("Merchant is not authenticated");
//         }
//         return merchant;
//    }
}
