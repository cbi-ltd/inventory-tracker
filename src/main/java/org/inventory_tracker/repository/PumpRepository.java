package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PumpRepository extends JpaRepository<Pump, Long> {
    long countByStation_Merchant_CamsMerchantId(
        String merchantId);
    List<Pump> findByStation_Merchant_CamsMerchantIdOrderByPumpNumberAsc(String camsMerchantId);
    
    List<Pump> findByStation_Merchant_IdAndActiveTrueOrderByPumpNumberAsc(Long merchantId);

    List<Pump> findByStation_Merchant_IdOrderByPumpNumberAsc(Long merchantId);

    Optional<Pump> findByIdAndStation_Merchant_Id(Long pumpId, Long merchantId);

    boolean existsByPumpNumber(String pumpNumber);

    boolean existsByPumpNumberAndStation_Id(String pumpNumber, Long stationId);

    boolean existsByPumpNumberAndStation_IdAndIdNot(String pumpNumber, Long stationId, Long id);

    List<Pump> findAllByOrderByPumpNumberAsc();

    List<Pump> findByStation_IdOrderByPumpNumberAsc(Long stationId);

    List<Pump> findByActiveTrueOrderByPumpNumberAsc();

    Pump findByTerminalSerialNumber(String terminalSerialNumber);

    long countByStationId(Long stationId);
}
