package org.inventory_tracker.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAuditMapper;
import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import java.math.BigDecimal;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class PumpAuditService {

    private final PumpAuditRepository pumpAuditRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpAuditMapper pumpAuditMapper;

    @Transactional
    public PumpAuditResponse createPumpAudit(
            CreatePumpAuditRequest request) {

        PumpAssignment assignment =
                pumpAssignmentRepository.findById(
                                request.getPumpAssignmentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pump assignment not found"));

        if (pumpAuditRepository.findByPumpAssignment_Id(
                assignment.getId()).isPresent()) {

            throw new DuplicateResourceException(
                    "Pump audit already exists for this assignment");
        }

        PumpAudit audit =
                pumpAuditMapper.toEntity(request);

        audit.setPumpAssignment(assignment);

        audit.setBusinessDate(
                assignment.getAssignmentDate());

        audit.setClockInTime(
                assignment.getCreatedAt());

        audit.setClockOutTime(
                assignment.getUpdatedAt());

        /*
         * These values will eventually come from:
         *
         * - Pump meter integration
         * - Admin reconciliation
         * - Automatic calculations
         */

        BigDecimal opening = BigDecimal.ZERO;
        BigDecimal closing = BigDecimal.ZERO;

        audit.setOpeningReading(opening);
        audit.setClosingReading(closing);

        audit.setTotalDispensed(closing.subtract(opening));
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
}