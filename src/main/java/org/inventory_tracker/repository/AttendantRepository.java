package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendantRepository extends JpaRepository<Attendant,Long> {
    Optional<Attendant> findByIdAndStation_Merchant_Id(Long id, Long merchantId);

    List<Attendant> findByStation_Merchant_IdOrderByFullNameAsc(Long merchantId);

    List<Attendant> findByStation_Merchant_IdAndActiveTrueOrderByFullNameAsc(Long merchantId);
    
    boolean existsByUsername(String username);

    List<Attendant> findAllByOrderByFullNameAsc();

    List<Attendant> findByStation_IdOrderByFullNameAsc(Long stationId);

    List<Attendant> findByActiveTrueOrderByFullNameAsc();
}
