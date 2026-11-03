package org.inventory_tracker.repository;

import org.inventory_tracker.entity.CompanyAccountTransaction;
import org.inventory_tracker.enums.CompanyAccountTransactionType;
import org.inventory_tracker.enums.FuelingSettlementType;
import org.inventory_tracker.enums.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;   


public interface CompanyAccountTransactionRepository extends JpaRepository<CompanyAccountTransaction, Long> {
    
    List<CompanyAccountTransaction>findByFuelingAgreement_IdOrderByTransactionDateDesc(Long agreementId);

    @Query("""
        SELECT t
        FROM CompanyAccountTransaction t
        JOIN t.fuelingAgreement fa
        JOIN t.sale s
        WHERE fa.merchant.id = :merchantId
          AND fa.station.id = :stationId
          AND fa.settlementType = :settlementType
          AND t.type = :transactionType
          AND (:businessDate IS NULL OR s.businessDate = :businessDate)
          AND (:startDate IS NULL OR s.businessDate >= :startDate)
          AND (:endDate IS NULL OR s.businessDate <= :endDate)
          AND (:shift IS NULL OR s.shift = :shift)
          AND s.shift IN :shifts
        ORDER BY s.businessDate DESC, t.transactionDate DESC
        """)
    List<CompanyAccountTransaction> findFuelingFilter(
            @Param("merchantId") Long merchantId,
            @Param("stationId") Long stationId,
            @Param("settlementType") FuelingSettlementType settlementType,
            @Param("transactionType") CompanyAccountTransactionType transactionType,
            @Param("businessDate") LocalDate businessDate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("shift") Shift shift,
            @Param("shifts") Collection<Shift> shifts);
}
