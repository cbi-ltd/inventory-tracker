package org.inventory_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.inventory_tracker.entity.OfflinePayment;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.Shift;


public interface OfflinePaymentRepository extends JpaRepository<OfflinePayment, Long> {

    @Query("""
        SELECT op
        FROM OfflinePayment op
        JOIN op.sale s
        WHERE s.station.id = :stationId
          AND s.station.merchant.id = :merchantId
          AND (:businessDate IS NULL OR s.businessDate = :businessDate)
          AND (:startDate IS NULL OR s.businessDate >= :startDate)
          AND (:endDate IS NULL OR s.businessDate <= :endDate)
          AND (:shift IS NULL OR s.shift = :shift)
          AND s.shift IN :shifts
        ORDER BY s.businessDate DESC, s.id DESC
        """)
    List<OfflinePayment> filterOfflinePayment(
            @Param("merchantId") Long merchantId,
            @Param("stationId") Long stationId,
            @Param("businessDate") LocalDate businessDate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("shift") Shift shift,
            @Param("shifts") Collection<Shift> shifts);

    Optional<OfflinePayment> findByIdAndMerchant_Id(Long id, Long merchantId);

    Optional<OfflinePayment> findByReferenceAndMerchant_Id(String reference, Long merchantId);

    Page<OfflinePayment> findByStation_IdAndMerchant_IdAndStatus(
            Long stationId,
            Long merchantId,
            PaymentStatus status,
            Pageable pageable);

    Page<OfflinePayment> findByStation_IdAndMerchant_Id(Long stationId, Long merchantId, Pageable pageable);

    boolean existsBySale_Id(Long saleId);

    Optional<OfflinePayment> findBySale_Id(Long saleId);

    Page<OfflinePayment> findAllByMerchant_Id(Long merchantId, Pageable pageable);

}