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
import lombok.Builder;
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
import org.inventory_tracker.enums.TerminalMode;
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

        if (!Boolean.TRUE.equals(assignment.getActive())) {
                throw new BadRequestException("Cannot create audit for an inactive pump assignment");
        }

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
        public PumpAuditResponse aboutToBeChangedClosePumpAudit(Long pumpAssignmentId, String terminalSerialNumber, Long pumpId, BigDecimal closingReading){
                if (closingReading == null) {
                        throw new BadRequestException("Closing meter reading is required");
                }

                PumpAssignment assignment;

                if (pumpAssignmentId != null) {
                        assignment = pumpAssignmentRepository.findById(pumpAssignmentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));

                        verifyAssignmentOwnership(assignment);

                } 
                else {
                        if (terminalSerialNumber == null || terminalSerialNumber.isBlank()) {
                                throw new BadRequestException("Terminal serial number is required");
                        }

                        Terminal terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                                        .orElseThrow(() -> new ResourceNotFoundException("Active terminal not found"));

                        Station station = terminal.getStation();

                        if (station == null) {
                                throw new ResourceNotFoundException("Terminal is not associated with a station");
                        }

                        if (station.getMerchant() == null) {
                                throw new ResourceNotFoundException("Station is not associated with a merchant");
                        }

                        LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
                        Shift shift = ShiftUtil.currentShift(station.getTimeZone());

                        if (station.getTerminalMode() == TerminalMode.MULTI_PUMP) {
                                if (pumpId == null) {
                                        throw new BadRequestException("Pump ID is required for multi-pump stations");
                                }

                                assignment =pumpAssignmentRepository
                                                .findByTerminalIdAndPumpIdAndAssignmentDateAndShiftAndActiveTrue(terminal.getId(), pumpId, today,shift)
                                                .orElseThrow(() -> new ResourceNotFoundException("No active assignment found for this terminal and pump"));
                        } 
                        else {
                                assignment = pumpAssignmentRepository
                                        .findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(terminal.getId(), today, shift)
                                        .orElseThrow(() -> new ResourceNotFoundException("No active assignment for this terminal"));
                        }

                        if (assignment.getTerminal() == null || !assignment.getTerminal().getId().equals(terminal.getId())) {
                                throw new BadRequestException("Assignment does not belong to this terminal");
                        }

                        if (assignment.getStation() == null || !assignment.getStation().getId().equals(station.getId())) {
                                throw new BadRequestException("Assignment does not belong to terminal's station");
                        }
                }

                if (!Boolean.TRUE.equals(assignment.getActive())) {
                        throw new BadRequestException("Pump assignment is already closed");
                }

                Terminal terminal = assignment.getTerminal();

                if (terminal == null) {
                        throw new ResourceNotFoundException("Assignment is not associated with a terminal");
                }

                if (!Boolean.TRUE.equals(terminal.getActive())) {
                        throw new BadRequestException("Terminal is inactive");
                }

                Pump pump = assignment.getPump();

                if (pump == null) {
                        throw new ResourceNotFoundException("Assignment is not associated with a pump");
                }

                PumpAudit audit = pumpAuditRepository.findByPumpAssignment_Id(assignment.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

                if (audit.getClockOutTime() != null) {
                        throw new BadRequestException("Pump audit is already closed");
                }

                BigDecimal openingReading = audit.getOpeningReading();

                if (openingReading == null) {
                        throw new BadRequestException("Opening meter reading is not set");
                }

                if (closingReading.compareTo(openingReading) < 0) {
                        throw new BadRequestException("Closing reading cannot be less than opening reading");
                }

                BigDecimal totalDispensed = closingReading.subtract(openingReading);

                audit.setClosingReading(closingReading);
                audit.setTotalDispensed(totalDispensed);

                Station station = assignment.getStation();

                if (station == null) {
                        throw new ResourceNotFoundException("Assignment is not associated with a station");
                }

                audit.setClockOutTime(LocalDateTime.now(station.getTimeZone()));
                assignment.setActive(false);

                pumpAssignmentRepository.save(assignment);
                PumpAudit savedAudit = pumpAuditRepository.save(audit);

                return pumpAuditMapper.toResponse(savedAudit);
        }


        @Transactional
        public PumpAuditResponse closePumpAuditFromWeb(Long pumpAssignmentId, BigDecimal closingReading) {
                if (closingReading == null) {
                        throw new BadRequestException("Closing meter reading is required");
                }

                if (pumpAssignmentId == null) {
                        throw new BadRequestException("Pump assignment ID is required");
                }

                PumpAssignment assignment = pumpAssignmentRepository.findById(pumpAssignmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pump assignment not found"));

                verifyAssignmentOwnership(assignment);
                return closeAudit(assignment, closingReading);
        }



        @Transactional
        public PumpAuditResponse closePumpAuditFromTerminal(String terminalSerialNumber, Long pumpId, BigDecimal closingReading) {
                if (closingReading == null) {
                        throw new BadRequestException("Closing meter reading is required");
                }

                if (terminalSerialNumber == null || terminalSerialNumber.isBlank()) {
                        throw new BadRequestException("Terminal serial number is required");
                }

                Terminal terminal = terminalRepository
                        .findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Active terminal not found"));

                Station station = terminal.getStation();

                if (station == null) {
                        throw new ResourceNotFoundException(
                                "Terminal is not associated with a station");
                }

                if (station.getMerchant() == null) {
                        throw new ResourceNotFoundException(
                                "Station is not associated with a merchant");
                }

                LocalDate today = ShiftUtil.businessDate(station.getTimeZone());
                Shift shift = ShiftUtil.currentShift(station.getTimeZone());

                PumpAssignment assignment;

                if (station.getTerminalMode() == TerminalMode.MULTI_PUMP) {

                        if (pumpId == null) {
                        throw new BadRequestException(
                                "Pump ID is required for multi-pump stations");
                        }

                        assignment = pumpAssignmentRepository
                                .findByTerminalIdAndPumpIdAndAssignmentDateAndShiftAndActiveTrue(
                                        terminal.getId(),
                                        pumpId,
                                        today,
                                        shift
                                )
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "No active assignment found for this terminal and pump"));
                } else {

                        assignment = pumpAssignmentRepository
                                .findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(
                                        terminal.getId(),
                                        today,
                                        shift
                                )
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "No active assignment for this terminal"));
                }

                // Extra consistency checks
                if (assignment.getTerminal() == null ||
                        !assignment.getTerminal().getId().equals(terminal.getId())) {

                        throw new BadRequestException(
                                "Assignment does not belong to this terminal");
                }

                if (assignment.getStation() == null ||
                        !assignment.getStation().getId().equals(station.getId())) {

                        throw new BadRequestException(
                                "Assignment does not belong to terminal's station");
                }

                return closeAudit(assignment, closingReading);
        }



    private PumpAuditResponse closeAudit(PumpAssignment assignment, BigDecimal closingReading) {
        if (!Boolean.TRUE.equals(assignment.getActive())) {
                throw new BadRequestException("Pump assignment is already closed");
        }

        Terminal terminal = assignment.getTerminal();
        if (terminal == null) {
                throw new ResourceNotFoundException("Assignment is not associated with a terminal");
        }

        if (!Boolean.TRUE.equals(terminal.getActive())) {
                throw new BadRequestException("Terminal is inactive");
        }

        Pump pump = assignment.getPump();
        if (pump == null) {
                throw new ResourceNotFoundException("Assignment is not associated with a pump");
        }

        PumpAudit audit = pumpAuditRepository
                .findByPumpAssignment_Id(assignment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pump audit not found"));

        if (audit.getClockOutTime() != null) {
                throw new BadRequestException("Pump audit is already closed");
        }

        BigDecimal openingReading = audit.getOpeningReading();
        if (openingReading == null) {
                throw new BadRequestException("Opening meter reading is not set");
        }

        if (closingReading.compareTo(openingReading) < 0) {
                throw new BadRequestException("Closing reading cannot be less than opening reading");
        }

        BigDecimal totalDispensed = closingReading.subtract(openingReading);

        audit.setClosingReading(closingReading);
        audit.setTotalDispensed(totalDispensed);

        Station station = assignment.getStation();

        if (station == null) {
                throw new ResourceNotFoundException("Assignment is not associated with a station");
        }

        audit.setClockOutTime(LocalDateTime.now(station.getTimeZone()));
        assignment.setActive(false);
        pumpAssignmentRepository.save(assignment);
        PumpAudit savedAudit = pumpAuditRepository.save(audit);

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
   public List<ShiftSummaryResponse> getShiftSummary(
        Long terminalId,
        String terminalSerialNumber,
        Long pumpId,
        LocalDate businessDate,
        Shift shift) {

    if (terminalId == null && terminalSerialNumber == null) {
        throw new BadRequestException("Provide terminalId or terminalSerialNumber");
    }

    if (terminalId != null && terminalSerialNumber != null) {
        throw new BadRequestException("Provide either terminalId or terminalSerialNumber, not both");
    }

    Terminal terminal;

    if (terminalId != null) {
        terminal = terminalRepository
                .findByIdAndActiveTrue(terminalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active terminal not found"));
    } 
    else {
        terminal = terminalRepository.findByTerminalSerialNumberAndActiveTrue(terminalSerialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Active terminal not found"));
    }

    Station station = terminal.getStation();
    if (station == null) {
        throw new ResourceNotFoundException("Terminal is not associated with a station");
    }

    final LocalDate resolvedBusinessDate = businessDate != null ? businessDate : ShiftUtil.businessDate(station.getTimeZone());

    final Shift resolvedShift = shift != null ? shift : ShiftUtil.currentShift(station.getTimeZone());

    List<PumpAssignment> assignments;

    if (pumpId != null) {
        PumpAssignment assignment = pumpAssignmentRepository
                        .findByTerminalIdAndPumpIdAndAssignmentDateAndShift(
                                terminal.getId(),
                                pumpId,
                                resolvedBusinessDate,
                                resolvedShift
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No assignment found for pump"
                                                + pumpId
                                                + " on terminal "
                                                + terminal.getTerminalSerialNumber()
                                                + " for "
                                                + resolvedBusinessDate
                                                + " "
                                                + resolvedShift
                                )
                        );

        assignments = List.of(assignment);

    } 
    else {
        assignments = pumpAssignmentRepository
                        .findAllByTerminalIdAndAssignmentDateAndShiftOrderByIdAsc(terminal.getId(), resolvedBusinessDate, resolvedShift);

        if (assignments.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No assignments found for terminal "
                            + terminal.getTerminalSerialNumber()
                            + " for "
                            + resolvedBusinessDate
                            + " "
                            + resolvedShift
            );
        }
    }

    return assignments.stream().map(assignment -> buildShiftSummary(assignment, terminal, station)).toList();
}




   private ShiftSummaryResponse buildShiftSummary(PumpAssignment assignment, Terminal terminal, Station station) {

        if (assignment.getTerminal() == null) {
                throw new BadRequestException("Assignment " + assignment.getId() + " is not associated with a terminal");
        }

        if (!assignment.getTerminal().getId().equals(terminal.getId())) {
                throw new BadRequestException("Assignment does not belong to the requested terminal");
        }


        if (assignment.getStation() == null) {
                throw new ResourceNotFoundException("Assignment is not associated with a station");
        }

        if (!assignment.getStation().getId().equals(station.getId())) {
                throw new BadRequestException("Assignment does not belong to terminal's station");
        }

        Pump pump = assignment.getPump();

        if (pump == null) {
                throw new ResourceNotFoundException("No pump assigned to assignment " + assignment.getId());
        }

        Attendant attendant = assignment.getAttendant();

        if (attendant == null) {
                throw new ResourceNotFoundException("No attendant assigned to assignment " + assignment.getId());
        }

        Product product = pump.getProduct();

        if (product == null) {
                throw new ResourceNotFoundException("No product configured for pump " + pump.getId());
        }

        PumpAudit audit =
                pumpAuditRepository
                        .findByPumpAssignment_Id(assignment.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump audit not found for assignment "
                                                + assignment.getId()
                                )
                        );

        BigDecimal litresSold = saleRepository.sumQuantityByPumpAndBusinessDateAndShift(pump.getId(), assignment.getAssignmentDate(), assignment.getShift());

        if (litresSold == null) { litresSold = BigDecimal.ZERO; }

        BigDecimal revenue = saleRepository.sumNetAmountByPumpAndBusinessDateAndShift(pump.getId(), assignment.getAssignmentDate(), assignment.getShift());

        if (revenue == null) { revenue = BigDecimal.ZERO; }

        StationInventory inventory = stationInventoryRepository
                        .findByStationIdAndProductId(station.getId(), product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for station " + station.getId() + " and product " + product.getId()));


        return ShiftSummaryResponse.builder()
                .stationId(station.getId())
                .stationName(station.getName())
                .pumpId(pump.getId())
                .pumpNumber(pump.getPumpNumber())
                .pumpName(pump.getPumpName())
                .attendantId(attendant.getId())
                .attendantName(attendant.getFullName())
                .terminalId(terminal.getId())
                .terminalSerialNumber(terminal.getTerminalSerialNumber())
                .businessDate(assignment.getAssignmentDate())
                .shift(assignment.getShift())
                .openingMeterReading(audit.getOpeningReading())
                .closingMeterReading(audit.getClosingReading())
                .totalLitresSold(litresSold)
                .totalRevenue(revenue)
                .currentSellingPrice(inventory.getSellingPrice())
                .build();
    }

    private void verifyStationOwnership(Station station) {
        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if (station == null || station.getMerchant() == null || !station.getMerchant().getId().equals(principal.getMerchantDbId())) {
                throw new ResourceNotFoundException("Resource not found");
        }
    }

    private void verifyAssignmentOwnership(PumpAssignment assignment) {
        if (assignment == null) { throw new ResourceNotFoundException("Pump assignment not found"); }
        verifyStationOwnership(assignment.getStation());
    }

    private void newVerifyAssignmentOwnership(PumpAssignment assignment, String camsMerchantId) {
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

        if (!Objects.equals(assignmentMerchantId, camsMerchantId)) {
                throw new ResourceNotFoundException("Assignment not found");
        }
    }
}