package org.inventory_tracker.repository;

import org.inventory_tracker.entity.FuelingCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface FuelingCompanyRepository extends JpaRepository<FuelingCompany, Long> {
    
    Optional<FuelingCompany> findByIdAndMerchant_IdAndActiveTrue(Long companyId, Long merchantId);

    List<FuelingCompany> findByMerchant_IdAndActiveTrueOrderByNameAsc(Long merchantId);

    Optional<FuelingCompany> findByIdAndMerchant_Id(Long id, Long merchantId);


    boolean existsByMerchant_IdAndNameIgnoreCase(Long merchantId, String name);

    boolean existsByMerchant_IdAndCodeIgnoreCase(Long merchantId, String code);

    boolean existsByMerchant_IdAndNameIgnoreCaseAndIdNot(
            Long merchantId,
            String name,
            Long id
    );

    boolean existsByMerchant_IdAndCodeIgnoreCaseAndIdNot(
            Long merchantId,
            String code,
            Long id
    );
}
