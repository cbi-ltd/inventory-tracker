package org.inventory_tracker.service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.dto.request.AssignPumpRequest;
import org.inventory_tracker.dto.request.ChangeTerminalAssignmentRequest;
import org.inventory_tracker.dto.response.PumpAssignmentResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.util.ShiftUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAssignmentMapper;
import java.util.List;
import org.inventory_tracker.repository.TerminalRepository;


@Service
@RequiredArgsConstructor
public class PumpAssignmentService {
    private final AttendantRepository attendantRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpRepository pumpRepository;
    private final PumpAssignmentMapper pumpAssignmentMapper;
    private final StationRepository stationRepository;
    private final TerminalRepository terminalRepository;


    @Transactional
    public PumpAssignmentResponse changeTerminalAssignment(Long assignmentId,
        ChangeTerminalAssignmentRequest request) {
        Merchant merchant = getAuthenticatedMerchant();
        PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump assignment not found"));
        if (!assignment.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Pump assignment not found");
        }

        Pump pump = assignment.getPump();
        Terminal terminal = terminalRepository.findById(request.getTerminalId())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        boolean terminalBelongsToPump = (pump.getDefaultTerminal() != null
                                                && pump.getDefaultTerminal().getId().equals(terminal.getId())) ||
                                        (pump.getTerminalSerialNumber() != null
                                                && pump.getTerminalSerialNumber().equals(terminal.getTerminalSerialNumber()));

        if (!terminalBelongsToPump) {
                throw new ResourceNotFoundException("Terminal not found");
        }

        assignment.setTerminal(terminal);
        PumpAssignment updated = pumpAssignmentRepository.save(assignment);

        return pumpAssignmentMapper.toResponse(updated);
    }

        
    @Transactional
    public PumpAssignmentResponse assignPumpToAttendant(AssignPumpRequest request) {
        Merchant merchant = getAuthenticatedMerchant();
        
        Station station = stationRepository.findById(request.getStationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        if (!station.getMerchant().getId().equals(merchant.getId())) {
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

        Terminal terminal = (pump.getDefaultTerminal() != null) ? pump.getDefaultTerminal() : terminalRepository.findByTerminalSerialNumber(pump.getTerminalSerialNumber())
                                .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));

        boolean terminalBelongsToPump = (pump.getDefaultTerminal() != null
                                                && pump.getDefaultTerminal().getId().equals(terminal.getId())) ||
                                        (pump.getTerminalSerialNumber() != null
                                                && pump.getTerminalSerialNumber().equals(terminal.getTerminalSerialNumber()));

        if (!terminalBelongsToPump) {
                throw new ResourceNotFoundException("Terminal not found");
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
        Merchant merchant = getAuthenticatedMerchant();
        PumpAssignment assignment = pumpAssignmentRepository.findByAttendantIdAndActiveTrue(attendantId)
                                        .orElseThrow(() -> new ResourceNotFoundException("No active pump assignment found."));

        if (!assignment.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("No active pump assignment found.");
        }
                                
        return pumpAssignmentMapper.toResponse(assignment);
     }

     @Transactional(readOnly = true)
     public List<PumpAssignmentResponse> getAttendantPumpAssignmentHistory(Long attendantId) {
        Merchant merchant = getAuthenticatedMerchant();
        Attendant attendant = attendantRepository.findById(attendantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendant not found"));
        
        if (!attendant.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Attendant not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                        .findByAttendantIdOrderByAssignmentDateDesc(attendantId));
     }

    @Transactional(readOnly = true)
    public List<PumpAssignmentResponse> getTodayPumpAssignments(Long stationId) {
        Merchant merchant = getAuthenticatedMerchant();
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        
        if (!station.getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Station not found");
        }

        LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                        .findByStationIdAndAssignmentDateOrderByPump_PumpNumberAsc(stationId,today));
    }

    @Transactional(readOnly = true)
    public List<PumpAssignmentResponse> getAllAssignments() {
        Merchant merchant = getAuthenticatedMerchant();
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                    .findByStation_Merchant_IdOrderByAssignmentDateDescShiftAsc(merchant.getId()));
    }

    @Transactional(readOnly = true)
    public PumpAssignmentResponse getAssignmentById(Long assignmentId) {
        Merchant merchant = getAuthenticatedMerchant();
        PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));
        
        if (!assignment.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Pump assignment not found");
        }

        return pumpAssignmentMapper.toResponse(assignment);
    }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByStation(Long stationId) {
       Merchant merchant = getAuthenticatedMerchant();
        Station station = stationRepository.findById(stationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
      
        if (!station.getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Station not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                     .findByStationIdOrderByAssignmentDateDescShiftAsc(stationId));
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByPump(Long pumpId) {
        Merchant merchant = getAuthenticatedMerchant();
        Pump pump = pumpRepository.findById(pumpId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
                
        if (!pump.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Pump not found");
        }

        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                        .findByPumpIdOrderByAssignmentDateDescShiftAsc(pumpId));
   }

   @Transactional(readOnly = true)
   public PumpAssignmentResponse getCurrentPumpAssignment(Long pumpId) {
        Merchant merchant = getAuthenticatedMerchant();
        Pump pump = pumpRepository.findById(pumpId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
                
        if (!pump.getStation().getMerchant().getId().equals(merchant.getId())) {
                throw new ResourceNotFoundException("Pump not found");
        }

        PumpAssignment assignment = pumpAssignmentRepository.findByPumpIdAndActiveTrue(pumpId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump has no active assignment."));

        return pumpAssignmentMapper.toResponse(assignment);
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByDate(LocalDate assignmentDate) {
        Merchant merchant = getAuthenticatedMerchant();
        return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                .findByStation_Merchant_IdAndAssignmentDateOrderByStation_NameAscPump_PumpNumberAsc(merchant.getId(), assignmentDate));
   }

   @Transactional(readOnly = true)
   public List<PumpAssignmentResponse> getAssignmentsByShift(Shift shift) {
        Merchant merchant = getAuthenticatedMerchant();

                return pumpAssignmentMapper.toResponseList(pumpAssignmentRepository
                                .findByStation_Merchant_IdAndShiftOrderByAssignmentDateDescStation_NameAscPump_PumpNumberAsc(merchant.getId(), shift));
   }

   private Merchant getAuthenticatedMerchant() {
        Merchant merchant = MerchantContext.getCurrentMerchant();
        if (merchant == null) {
                throw new ResourceNotFoundException("Merchant is not authenticated");
        }
        return merchant;
   }
}
