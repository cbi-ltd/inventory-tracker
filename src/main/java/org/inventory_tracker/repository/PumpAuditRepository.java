package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PumpAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PumpAuditRepository extends JpaRepository<PumpAudit, Long> {

    Optional<PumpAudit> findByPumpAssignment_Id(Long assignmentId);

    List<PumpAudit> findAllByOrderByBusinessDateDesc();

    List<PumpAudit> findByBusinessDateOrderByClockInTimeAsc(
            LocalDate businessDate
    );

    List<PumpAudit> findByPumpAssignment_Pump_IdOrderByBusinessDateDesc(
            Long pumpId
    );

    List<PumpAudit> findByPumpAssignment_Attendant_IdOrderByBusinessDateDesc(
            Long attendantId
    );

    List<PumpAudit> findByPumpAssignment_Station_IdOrderByBusinessDateDesc(
            Long stationId
    );
}