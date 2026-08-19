package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.enums.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PumpAssignmentRepository extends JpaRepository<PumpAssignment, Long> {
        Optional<PumpAssignment>findFirstByTerminal_TerminalSerialNumberAndTerminal_Station_Merchant_CamsMerchantIdAndActiveTrueOrderByAssignmentDateDesc(
        String terminalSerialNumber,
        String camsMerchantId
);

        Optional<PumpAssignment>
    findByTerminal_IdAndTerminal_Station_Merchant_CamsMerchantIdAndAssignmentDateAndShiftAndActiveTrue(
            Long terminalId,
            String camsMerchantId,
            LocalDate assignmentDate,
            Shift shift
    );

    Optional<PumpAssignment>findFirstByTerminal_IdAndPump_IdAndActiveTrue(Long terminalId, Long pumpId);

    Optional<PumpAssignment>
    findByTerminal_TerminalSerialNumberAndTerminal_Station_Merchant_CamsMerchantIdAndAssignmentDateAndShiftAndActiveTrue(
            String terminalSerialNumber,
            String camsMerchantId,
            LocalDate assignmentDate,
            Shift shift
    );

    Optional<PumpAssignment> findById(Long id);

    long countByStation_Merchant_CamsMerchantIdAndActiveTrue(
        String merchantId);
    List<PumpAssignment> findByStation_Merchant_CamsMerchantId(String camsMerchantId);
    
    List<PumpAssignment> findByStation_Merchant_IdAndAssignmentDateOrderByPump_PumpNumberAsc(Long merchantId, LocalDate assignmentDate);

    List<PumpAssignment>findByStation_Merchant_IdAndShiftOrderByAssignmentDateDescStation_NameAscPump_PumpNumberAsc(Long merchantId, Shift shift);
    
    List<PumpAssignment>findByStation_Merchant_IdAndAssignmentDateOrderByStation_NameAscPump_PumpNumberAsc(Long merchantId, LocalDate assignmentDate);
    
    List<PumpAssignment>findByStation_Merchant_IdOrderByAssignmentDateDescShiftAsc(Long merchantId);

    Optional<PumpAssignment> findByPumpIdAndAssignmentDateAndShiftAndActiveTrue(Long pumpId, LocalDate assignmentDate, Shift shift);

    Optional<PumpAssignment> findByAttendantIdAndActiveTrue(Long attendantId);

    List<PumpAssignment> findByAttendantIdOrderByAssignmentDateDesc(Long attendantId);

    List<PumpAssignment> findByStationIdAndAssignmentDateOrderByPump_PumpNumberAsc(Long stationId, LocalDate assignmentDate);

    List<PumpAssignment> findByAssignmentDate(LocalDate assignmentDate);

    List<PumpAssignment> findByStationId(Long stationId);

    List<PumpAssignment> findAllByAttendantIdAndActiveTrue(Long attendantId);

    long countByStationIdAndActiveTrue(Long stationId);

    long countByPumpStationIdAndActiveTrue(Long stationId);

    long countByActiveTrue();

    long countByPumpId(Long pumpId);

    long countByPumpIdAndActiveTrue(Long pumpId);

    Optional<PumpAssignment> findByTerminal_TerminalSerialNumberAndAssignmentDateAndShiftAndActiveTrue(String terminalSerialNumber, LocalDate assignmentDate, Shift shift);

    Optional<PumpAssignment> findFirstByTerminalIdAndActiveTrueOrderByAssignmentDateDesc(Long terminalId);

    Optional<PumpAssignment> findFirstByTerminal_TerminalSerialNumberAndActiveTrueOrderByAssignmentDateDesc(String terminalSerialNumber);
    
    Optional<PumpAssignment>findByTerminalIdAndAssignmentDateAndShiftAndActiveTrue(Long terminalId, LocalDate assignmentDate, Shift shift);

    Optional<PumpAssignment> findFirstByAttendantIdAndActiveTrue(Long attendantId);

    List<PumpAssignment> findAllByOrderByAssignmentDateDescShiftAsc();

    List<PumpAssignment> findByStationIdOrderByAssignmentDateDescShiftAsc(Long stationId);

    List<PumpAssignment> findByPumpIdOrderByAssignmentDateDescShiftAsc(Long pumpId);

    Optional<PumpAssignment> findByPumpIdAndActiveTrue(Long pumpId);
    
    Optional<PumpAssignment> findFirstByPumpIdAndActiveTrue(Long pumpId);

    List<PumpAssignment> findByAssignmentDateOrderByStation_NameAscPump_PumpNumberAsc(LocalDate assignmentDate);

    List<PumpAssignment> findByShiftOrderByAssignmentDateDescStation_NameAscPump_PumpNumberAsc(Shift shift);
}