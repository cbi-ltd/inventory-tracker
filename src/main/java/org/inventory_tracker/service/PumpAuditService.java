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

import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Product;
// import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.Terminal;
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.security.MerchantPrincipal;
import org.inventory_tracker.util.ShiftUtil;
import org.inventory_tracker.entity.specification.PumpAuditSpecification;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.TerminalRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.repository.SaleRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;



@Service
@RequiredArgsConstructor
public class PumpAuditService {
    private final AttendantRepository attendantRepository;
    private final PumpRepository pumpRepository;
    private final StationRepository stationRepository;
    private final PumpAuditRepository pumpAuditRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditMapper pumpAuditMapper;
    private final SaleRepository saleRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final TerminalRepository terminalRepository;


    @Transactional
    public PumpAuditResponse createPumpAudit(CreatePumpAuditRequest request) {
        PumpAssignment assignment = pumpAssignmentRepository.findById(request.getPumpAssignmentId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));
        verifyAssignmentOwnership(assignment);

        if (pumpAuditRepository.findByPumpAssignment_Id(assignment.getId()).isPresent()) {
            throw new DuplicateResourceException("Pump audit already exists for this assignment");
        }

        PumpAudit audit = pumpAuditMapper.toEntity(request);
        audit.setPumpAssignment(assignment);
        audit.setBusinessDate(assignment.getAssignmentDate());
        audit.setClockInTime(assignment.getCreatedAt());
        audit.setClockOutTime(null);
        // audit.setClockOutTime(assignment.getUpdatedAt());

        BigDecimal openingReading = BigDecimal.ZERO;
        Optional<PumpAudit> previousAudit = pumpAuditRepository
                                        .findTopByPumpAssignment_PumpIdOrderByBusinessDateDescClockInTimeDesc(assignment.getPump().getId());

        if (previousAudit.isPresent()) { openingReading = previousAudit.get().getClosingReading(); }

        audit.setOpeningReading(openingReading);
        audit.setClosingReading(openingReading);
        audit.setTotalDispensed(BigDecimal.ZERO);

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
        verifyAssignmentOwnership(audit.getPumpAssignment());

        return pumpAuditMapper.toResponse(audit);
    }

    @Transactional
    public List<PumpAuditResponse> getAllPumpAudits() {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return pumpAuditMapper.toResponseList(pumpAuditRepository
                .findByPumpAssignment_Station_Merchant_IdOrderByBusinessDateDesc(principal.getMerchantDbId()));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Station not found"));
        verifyStationOwnership(station);
        return pumpAuditMapper.toResponseList(pumpAuditRepository
                        .findByPumpAssignment_Station_IdOrderByBusinessDateDesc(stationId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByPump(Long pumpId) {
        Pump pump = pumpRepository.findById(pumpId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));
        verifyStationOwnership(pump.getStation());
        
        return pumpAuditMapper.toResponseList(pumpAuditRepository
                        .findByPumpAssignment_Pump_IdOrderByBusinessDateDesc(pumpId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByAttendant(Long attendantId) {
        Attendant attendant = attendantRepository.findById(attendantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attendant not found"));
        verifyStationOwnership(attendant.getStation());
        
        return pumpAuditMapper.toResponseList(pumpAuditRepository
                        .findByPumpAssignment_Attendant_IdOrderByBusinessDateDesc(attendantId));
    }

    @Transactional
    public List<PumpAuditResponse> getPumpAuditsByBusinessDate(LocalDate businessDate) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return pumpAuditMapper.toResponseList(pumpAuditRepository
                        .findByPumpAssignment_Station_Merchant_IdAndBusinessDateOrderByClockInTimeAsc(principal.getMerchantDbId(),businessDate));
    }

    @Transactional
    public PumpAuditResponse getPumpAuditByAssignment(Long assignmentId) {
        PumpAssignment assignment = pumpAssignmentRepository.findById(assignmentId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));
        verifyAssignmentOwnership(assignment);

        PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignmentId)
                                .orElseThrow(() ->new ResourceNotFoundException("Pump audit not found"));

        return pumpAuditMapper.toResponse(audit);
    }

    @Transactional
    public PumpAuditResponse closePumpAudit(String terminalSerialNumber, BigDecimal closingReading) {
        if (terminalSerialNumber == null || terminalSerialNumber.isBlank()) {
                throw new BadRequestException("Terminal serial number is required");
        }

        if (closingReading == null) {
                throw new BadRequestException("Closing meter reading is required");
        }

        Terminal terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Active terminal not found"));

        Station station = terminal.getStation();

        if (station == null) {
                throw new ResourceNotFoundException("Terminal is not associated with a station");
        }

        Merchant merchant = station.getMerchant();

        if (merchant == null) {
                throw new ResourceNotFoundException("Station is not associated with a merchant");
        }

        // PumpAssignment assignment = pumpAssignmentRepository.findFirstByTerminal_TerminalSerialNumberAndActiveTrueOrderByAssignmentDateDesc(terminalSerialNumber)
        //             .orElseThrow(() -> new ResourceNotFoundException("No active assignment found for terminal"));

        PumpAssignment assignment = pumpAssignmentRepository
                .findFirstByTerminal_TerminalSerialNumberAndTerminal_Station_Merchant_CamsMerchantIdAndActiveTrueOrderByAssignmentDateDesc(
                        terminalSerialNumber,
                        merchant.getCamsMerchantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active assignment found for terminal"));

        if (!assignment.getActive()) {
            throw new BadRequestException(
                    "Pump assignment is already closed");
        }

        if (!assignment.getTerminal().getId().equals(terminal.getId())) {
                throw new BadRequestException("Assignment does not belong to this terminal");
        }

        PumpAudit audit =
                pumpAuditRepository
                        .findByPumpAssignment_Id(assignment.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump audit not found"));

        if (audit.getClockOutTime() != null) {
                throw new BadRequestException(
                        "Pump audit is already closed");
        }

        BigDecimal openingReading = audit.getOpeningReading();

        if (openingReading == null) {
                throw new BadRequestException(
                        "Opening meter reading is not set");
        }

        if (closingReading.compareTo(openingReading) < 0) {
                throw new BadRequestException(
                        "Closing reading cannot be less than opening reading");
        }

        BigDecimal totalDispensed =
        closingReading.subtract(openingReading);

        audit.setClosingReading(closingReading);
        audit.setTotalDispensed(totalDispensed);
        audit.setClockOutTime(
                LocalDateTime.now(station.getTimeZone()));

        assignment.setActive(false);

        pumpAssignmentRepository.save(assignment);

        PumpAudit savedAudit =
                pumpAuditRepository.save(audit);

        return pumpAuditMapper.toResponse(savedAudit);
    }


    @Transactional(readOnly = true)
    public List<PumpAuditResponse> filterPumpAudits(PumpAuditFilterRequest request) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        return pumpAuditMapper.toResponseList(pumpAuditRepository.findAll(PumpAuditSpecification.filter(request, principal.getMerchantDbId())));
    }

    @Transactional(readOnly = true)
    public ShiftSummaryResponse getTerminalShiftSummary(Long terminalId, String terminalSerialNumber) {
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

        //POS uses this functionality, so we cant do merchant-scoped authentication since POS dont log in
        // verifyAssignmentOwnership(assignment);
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

        @Transactional(readOnly = true)
        public ShiftSummaryResponse getShiftSummary(String terminalSerialNumber) {
                Terminal terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                                .orElseThrow(() -> new ResourceNotFoundException("Active terminal not found"));

                Station station = terminal.getStation();

                if (station == null) {
                        throw new ResourceNotFoundException("Terminal is not associated with a station");
                }

                Merchant merchant = station.getMerchant();

                if (merchant == null) {
                        throw new ResourceNotFoundException("Station is not associated with a merchant");
                }

                LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
                Shift shift = ShiftUtil.currentShift(station.getTimeZone());
                PumpAssignment assignment = pumpAssignmentRepository
                                .findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(
                                        terminal.getId(),
                                        today,
                                        shift)
                                .orElseThrow(() -> new ResourceNotFoundException("No active assignment for this terminal"));

                if (!assignment.getStation().getId().equals(station.getId())) {
                        throw new BadRequestException("Terminal assignment does not belong to terminal's station");
                }

                Pump pump = assignment.getPump();
                if (pump == null) {
                        throw new ResourceNotFoundException("No pump assigned to this terminal");
                }

                Product product = pump.getProduct();
                if (product == null) {
                        throw new ResourceNotFoundException("No product configured for this pump");
                }

                PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

                BigDecimal litresSold = saleRepository.sumQuantityByPumpAndMerchantAndBusinessDateAndShift(
                                        pump.getId(),
                                        merchant.getCamsMerchantId(),
                                        assignment.getAssignmentDate(),
                                        assignment.getShift());

                if (litresSold == null) { litresSold = BigDecimal.ZERO; }

                BigDecimal revenue = saleRepository.sumNetAmountByPumpAndMerchantAndBusinessDateAndShift(
                                        pump.getId(),
                                        merchant.getCamsMerchantId(),
                                        assignment.getAssignmentDate(),
                                        assignment.getShift());

                if (revenue == null) { revenue = BigDecimal.ZERO; }

                StationInventory inventory = stationInventoryRepository
                                .findByStation_IdAndStation_Merchant_CamsMerchantIdAndProduct_Id(
                                        station.getId(),
                                        merchant.getCamsMerchantId(),
                                        product.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

                ShiftSummaryResponse response =new ShiftSummaryResponse();

                response.setStationId(station.getId());
                response.setStationName(station.getName());
                response.setPumpId(pump.getId());
                response.setPumpNumber(pump.getPumpNumber());
                response.setPumpName(pump.getPumpName());
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

    private void verifyStationOwnership(Station station) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if (station == null || station.getMerchant() == null || !station.getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Resource not found");
        }
    }

    private void verifyAssignmentOwnership(PumpAssignment assignment) {
        if (assignment == null) {
                throw new ResourceNotFoundException("Pump assignment not found");
        }
        verifyStationOwnership(assignment.getStation());
    }

    private void newVerifyAssignmentOwnership(
        PumpAssignment assignment,
        String camsMerchantId) {

    if (assignment == null
            || assignment.getTerminal() == null
            || assignment.getTerminal().getStation() == null
            || assignment.getTerminal().getStation().getMerchant() == null) {

        throw new ResourceNotFoundException(
                "Assignment ownership could not be verified"
        );
    }

    String assignmentMerchantId = assignment
            .getTerminal()
            .getStation()
            .getMerchant()
            .getCamsMerchantId();

    if (!Objects.equals(
            assignmentMerchantId,
            camsMerchantId)) {

        throw new ResourceNotFoundException(
                "Assignment not found"
        );
    }
}
}