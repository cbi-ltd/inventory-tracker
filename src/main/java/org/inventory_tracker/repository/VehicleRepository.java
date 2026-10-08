package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByMerchant_IdAndActiveTrueOrderByRegistrationNumberAsc(
            Long merchantId
    );

    List<Vehicle> findByCompany_IdAndMerchant_IdAndActiveTrueOrderByRegistrationNumberAsc(
            Long companyId,
            Long merchantId
    );

    Optional<Vehicle> findByIdAndMerchant_Id(
            Long id,
            Long merchantId
    );

    Optional<Vehicle> findByIdAndMerchant_IdAndActiveTrue(
            Long id,
            Long merchantId
    );

    boolean existsByMerchant_IdAndRegistrationNumberIgnoreCase(
            Long merchantId,
            String registrationNumber
    );

    boolean existsByMerchant_IdAndRegistrationNumberIgnoreCaseAndIdNot(
            Long merchantId,
            String registrationNumber,
            Long id
    );
}
