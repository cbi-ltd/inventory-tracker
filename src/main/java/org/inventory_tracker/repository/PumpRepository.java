package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Pump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PumpRepository extends JpaRepository<Pump, Long> {

    boolean existsByPumpNumber(String pumpNumber);

    boolean existsByPumpNumberAndStation_Id(
            String pumpNumber,
            Long stationId
    );

    boolean existsByPumpNumberAndStation_IdAndIdNot(String pumpNumber, 
        Long stationId,
        Long id);

    List<Pump> findAllByOrderByPumpNumberAsc();

    List<Pump> findByStation_IdOrderByPumpNumberAsc(
            Long stationId
    );

    List<Pump> findByActiveTrueOrderByPumpNumberAsc();

    long countByStationId(Long stationId);
}
