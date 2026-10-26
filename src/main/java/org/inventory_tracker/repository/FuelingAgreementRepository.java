package org.inventory_tracker.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.inventory_tracker.entity.FuelingAgreement;
import org.inventory_tracker.enums.FuelingSettlementType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface FuelingAgreementRepository extends JpaRepository<FuelingAgreement, Long> {
    
    List<FuelingAgreement> findByCompany_NameContainingIgnoreCaseAndMerchant_IdAndActiveTrue(
        String companyName,
        Long merchantId
    );

    List<FuelingAgreement> findByMerchant_IdAndSettlementTypeAndActiveTrue(
        Long merchantId,
        FuelingSettlementType settlementType);

    Optional<FuelingAgreement> findFirstByCompany_IdAndMerchant_IdAndActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByIdDesc(
        Long companyId,
        Long merchantId,
        LocalDate date,
        LocalDate date2
    );

    List<FuelingAgreement> findByCompany_IdAndMerchant_IdAndActiveTrue(Long companyId, Long merchantId);

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
