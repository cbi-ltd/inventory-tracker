package org.inventory_tracker.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.inventory_tracker.entity.FuelingAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface FuelingAgreementRepository extends JpaRepository<FuelingAgreement, Long> {
    
    Optional<FuelingAgreement> findFirstByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(
        Long companyId,
        Long merchantId,
        LocalDate date,
        LocalDate date2
    );

    List<FuelingAgreement> findByMerchant_IdOrderByStartDateDesc(Long merchantId);

    List<FuelingAgreement> findByCompany_IdAndMerchant_IdOrderByStartDateDesc(
            Long companyId,
            Long merchantId);

    Optional<FuelingAgreement> findByIdAndMerchant_Id(
            Long id,
            Long merchantId);

    boolean existsByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long companyId,
            Long merchantId,
            LocalDate date1,
            LocalDate date2);

    boolean existsByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIdNot(
            Long companyId,
            Long merchantId,
            LocalDate date1,
            LocalDate date2,
            Long agreementId);
}
