package org.inventory_tracker.service;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAuditMapper;
import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.request.PumpAuditFilterRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.inventory_tracker.dto.response.ShiftSummaryResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.specification.PumpAuditSpecification;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.SaleRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PumpAuditService {

    private final PumpAuditRepository pumpAuditRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditMapper pumpAuditMapper;
    private final SaleRepository saleRepository;
    private final StationInventoryRepository stationInventoryRepository;

    @Transactional
    public PumpAuditResponse createPumpAudit(CreatePumpAuditRequest request) {
        PumpAssignment assignment = pumpAssignmentRepository.findById(request.getPumpAssignmentId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));

        if (pumpAuditRepository.findByPumpAssignment_Id(assignment.getId()).isPresent()) {
            throw new DuplicateResourceException("Pump audit already exists for this assignment");
        }

        PumpAudit audit = pumpAuditMapper.toEntity(request);
        audit.setPumpAssignment(assignment);
        audit.setBusinessDate(assignment.getAssignmentDate());
        audit.setClockInTime(assignment.getCreatedAt());
        audit.setClockOutTime(assignment.getUpdatedAt());

        /*
         * These values will eventually come from:
         *
         * - Pump meter integration
         * - Admin reconciliation
         * - Automatic calculations
         */

        // BigDecimal opening;
        // BigDecimal closing;

        BigDecimal openingReading = BigDecimal.ZERO;
        Optional<PumpAudit> previousAudit = pumpAuditRepository
                                        .findTopByPumpAssignment_PumpIdOrderByBusinessDateDescClockInTimeDesc(assignment.getPump().getId());

        if (previousAudit.isPresent()) { openingReading = previousAudit.get().getClosingReading(); }

        audit.setOpeningReading(openingReading);
        audit.setClosingReading(openingReading);
        audit.setTotalDispensed(BigDecimal.ZERO);

        // audit.setOpeningReading(opening);
        // audit.setClosingReading(closing);
        // audit.setTotalDispensed(closing.subtract(opening));

        PumpAudit saved = pumpAuditRepository.save(audit);
        return pumpAuditMapper.toResponse(saved);
    }

    @Transactional
    public PumpAuditResponse getPumpAuditById(Long id) {

        PumpAudit audit =
                pumpAuditRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump audit not found"));

        return pumpAuditMapper.toResponse(audit);
    }

    @Transactional
    public List<PumpAuditResponse> getAllPumpAudits() {

        return pumpAuditMapper.toResponseList(
                pumpAuditRepository
                        .findAllByOrderByBusinessDateDesc());
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByStation(
            Long stationId) {

        return pumpAuditMapper.toResponseList(
                pumpAuditRepository
                        .findByPumpAssignment_Station_IdOrderByBusinessDateDesc(
                                stationId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByPump(
            Long pumpId) {

        return pumpAuditMapper.toResponseList(
                pumpAuditRepository
                        .findByPumpAssignment_Pump_IdOrderByBusinessDateDesc(
                                pumpId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByAttendant(
            Long attendantId) {

        return pumpAuditMapper.toResponseList(
                pumpAuditRepository
                        .findByPumpAssignment_Attendant_IdOrderByBusinessDateDesc(
                                attendantId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByBusinessDate(
            LocalDate businessDate) {

        return pumpAuditMapper.toResponseList(
                pumpAuditRepository
                        .findByBusinessDateOrderByClockInTimeAsc(
                                businessDate));
    }

    @Transactional
    public PumpAuditResponse getPumpAuditByAssignment(
            Long assignmentId) {

        PumpAudit audit =
                pumpAuditRepository
                        .findByPumpAssignment_Id(
                                assignmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump audit not found"));

        return pumpAuditMapper.toResponse(audit);
    }


    @Transactional(readOnly = true)
    public List<PumpAuditResponse> filterPumpAudits(PumpAuditFilterRequest request) {
        return pumpAuditMapper.toResponseList(pumpAuditRepository.findAll(PumpAuditSpecification.filter(request)));
    }

    @Transactional(readOnly = true)
    public ShiftSummaryResponse getShiftSummary(Long terminalId, String terminalSerialNumber) {
        PumpAssignment assignment;
        if (terminalId != null) {
                assignment = pumpAssignmentRepository
                        .findFirstByTerminalIdAndActiveTrueOrderByAssignmentDateDesc(terminalId)
                        .orElseThrow(() -> new ResourceNotFoundException("No active assignment found"));
        } 
        else if (terminalSerialNumber != null) {
                assignment = pumpAssignmentRepository
                        .findFirstByTerminal_TerminalSerialNumberAndActiveTrueOrderByAssignmentDateDesc(terminalSerialNumber)
                        .orElseThrow(() -> new ResourceNotFoundException("No active assignment found"));
        } 
        else { throw new BadRequestException("Provide terminalId or terminalSerialNumber"); }

        PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

        BigDecimal litresSold = saleRepository.sumQuantityByPumpAndBusinessDateAndShift(
        assignment.getPump().getId(),
        assignment.getAssignmentDate(),
        assignment.getShift());

        if (litresSold == null) litresSold = BigDecimal.ZERO;

        BigDecimal revenue = saleRepository.sumNetAmountByPumpAndBusinessDateAndShift(
        assignment.getPump().getId(),
        assignment.getAssignmentDate(),
        assignment.getShift());

        if (revenue == null) revenue = BigDecimal.ZERO;

        StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(
        assignment.getStation().getId(),
        assignment.getPump().getProduct().getId()).orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        ShiftSummaryResponse response = new ShiftSummaryResponse();
        response.setStationId(assignment.getStation().getId());
        response.setStationName(assignment.getStation().getName());
        response.setPumpId(assignment.getPump().getId());
        response.setPumpNumber(assignment.getPump().getPumpNumber());
        response.setPumpName(assignment.getPump().getPumpName());
        response.setAttendantId(assignment.getAttendant().getId());
        response.setAttendantName(assignment.getAttendant().getFullName());
        response.setBusinessDate(assignment.getAssignmentDate());
        response.setShift(assignment.getShift());
        response.setOpeningMeterReading(audit.getOpeningReading());
        response.setClosingMeterReading(audit.getClosingReading());
        response.setTotalLitresSold(litresSold);
        response.setTotalRevenue(revenue);
        response.setCurrentSellingPrice(inventory.getSellingPrice());

        return response;
    }
}