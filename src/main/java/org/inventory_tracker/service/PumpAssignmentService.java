package org.inventory_tracker.service;

import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.dto.request.AssignPumpRequest;
import org.inventory_tracker.dto.response.PumpAssignmentResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.exception.DuplicateResourceException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.util.ShiftUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.PumpAssignmentMapper;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PumpAssignmentService {
    private final AttendantRepository attendantRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PumpRepository pumpRepository;
    private final PumpAssignmentMapper pumpAssignmentMapper;
    private final StationRepository stationRepository;


        
    @Transactional
    public PumpAssignmentResponse assignPumpToAttendant(AssignPumpRequest request) {

        Attendant attendant = attendantRepository.findById(request.getAttendantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendant not found"));

        Pump pump = pumpRepository.findById(request.getPumpId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pump not found"));

        Station station = stationRepository.findById(request.getStationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Station not found"));

        LocalDate today = LocalDate.now();
        Shift currentShift = ShiftUtil.currentShift();

        pumpAssignmentRepository
                .findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(
                        pump.getId(),
                        today,
                        currentShift
                )
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Pump " + pump.getPumpNumber()
                                    + " is already assigned for the current shift."
                    );
                });

        List<PumpAssignment> activeAssignments =
                pumpAssignmentRepository
                        .findAllByAttendantIdAndActiveTrue(attendant.getId());

        if (!activeAssignments.isEmpty()) {

            activeAssignments.forEach(a -> a.setActive(false));

            pumpAssignmentRepository.saveAll(activeAssignments);
        }

        PumpAssignment assignment = new PumpAssignment();
        assignment.setPump(pump);
        assignment.setAttendant(attendant);
        assignment.setStation(station);
        assignment.setAssignmentDate(today);
        assignment.setShift(currentShift);
        assignment.setActive(true);

        PumpAssignment saved =
                pumpAssignmentRepository.save(assignment);

        return pumpAssignmentMapper.toResponse(saved);
    }

        @Transactional(readOnly = true)
    public PumpAssignmentResponse getPumpCurrentAssignment(Long attendantId) {

        PumpAssignment assignment =
                pumpAssignmentRepository
                        .findByAttendantIdAndActiveTrue(
                                attendantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No active pump assignment found."
                                ));

        return pumpAssignmentMapper.toResponse(
                assignment);
     }

     @Transactional(readOnly = true)
     public List<PumpAssignmentResponse> getAttendantPumpAssignmentHistory(Long attendantId) {

        if (!attendantRepository.existsById(attendantId)) {
                throw new ResourceNotFoundException(
                        "Attendant not found");
        }

        return pumpAssignmentMapper.toResponseList(

                pumpAssignmentRepository
                        .findByAttendantIdOrderByAssignmentDateDesc(
                                attendantId
                        )
        );
     }

    @Transactional(readOnly = true)
    public List<PumpAssignmentResponse> getTodayPumpAssignments(Long stationId) {

        if (!stationRepository.existsById(stationId)) {
                throw new ResourceNotFoundException(
                        "Station not found");
        }

        return pumpAssignmentMapper.toResponseList(

                pumpAssignmentRepository
                        .findByStationIdAndAssignmentDateOrderByPump_PumpNumberAsc(
                                stationId,
                                LocalDate.now()
                        )
        );
    }
}
