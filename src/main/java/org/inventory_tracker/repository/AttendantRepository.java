package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendantRepository extends JpaRepository<Attendant,Long> {
    boolean existsByUsername(String username);

    List<Attendant> findAllByOrderByFullNameAsc();

    List<Attendant> findByStation_IdOrderByFullNameAsc(Long stationId);

    List<Attendant> findByActiveTrueOrderByFullNameAsc();
}
