package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.enums.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PumpAssignmentRepository
        extends JpaRepository<PumpAssignment, Long> {

    Optional<PumpAssignment> findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(
            Long pumpId,
            LocalDate assignmentDate,
            Shift shift
    );

    Optional<PumpAssignment> findByAttendantIdAndActiveTrue(
            Long attendantId
    );

    List<PumpAssignment> findByAttendantIdOrderByAssignmentDateDesc(
            Long attendantId
    );

    List<PumpAssignment> findByStationIdAndAssignmentDateOrderByPump_PumpNumberAsc(
            Long stationId,
            LocalDate assignmentDate
    );

    List<PumpAssignment> findByAssignmentDate(
            LocalDate assignmentDate
    );

    List<PumpAssignment> findByStationId(Long stationId);

    List<PumpAssignment> findAllByAttendantIdAndActiveTrue(Long attendantId);

    long countByStationIdAndActiveTrue(Long stationId);
    long countByPumpStationIdAndActiveTrue(Long stationId);

    long countByActiveTrue();

    long countByPumpId(Long pumpId);

    long countByPumpIdAndActiveTrue(Long pumpId);

    Optional<PumpAssignment> findFirstByAttendantIdAndActiveTrue(Long attendantId);

    List<PumpAssignment> findAllByOrderByAssignmentDateDescShiftAsc();

    List<PumpAssignment> findByStationIdOrderByAssignmentDateDescShiftAsc(Long stationId);

    List<PumpAssignment> findByPumpIdOrderByAssignmentDateDescShiftAsc(Long pumpId);

    Optional<PumpAssignment> findByPumpIdAndActiveTrue(Long pumpId);

    List<PumpAssignment> findByAssignmentDateOrderByStation_NameAscPump_PumpNumberAsc(LocalDate assignmentDate);

    List<PumpAssignment> findByShiftOrderByAssignmentDateDescStation_NameAscPump_PumpNumberAsc(Shift shift);
}