package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AttendantRepository extends JpaRepository<Attendant,Long> {
    Optional<Attendant> findByIdAndStation_Merchant_CamsMerchantId(Long id, String merchantId);

    List<Attendant> findByStation_Merchant_CamsMerchantIdOrderByFullNameAsc(String merchantId);

    List<Attendant> findByStation_Merchant_CamsMerchantIdAndActiveTrueOrderByFullNameAsc(String merchantId);
    
    boolean existsByUsername(String username);

    List<Attendant> findAllByOrderByFullNameAsc();

    List<Attendant> findByStation_IdOrderByFullNameAsc(Long stationId);

    List<Attendant> findByActiveTrueOrderByFullNameAsc();
}
