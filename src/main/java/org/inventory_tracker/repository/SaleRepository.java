package org.inventory_tracker.repository;


import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    Optional<Sale> findByTransactionReference(String transactionReference);

    Optional<Sale> findByReceiptNumber(String receiptNumber);

    List<Sale> findAllByOrderByBusinessDateDescSaleTimeDesc();

    List<Sale> findByBusinessDate(LocalDate businessDate);

    List<Sale> findByBusinessDateBetween(
            LocalDate startDate,
            LocalDate endDate);

    List<Sale> findByStationIdOrderByBusinessDateDescSaleTimeDesc(
            Long stationId);

    List<Sale> findByPumpIdOrderByBusinessDateDescSaleTimeDesc(
            Long pumpId);

    List<Sale> findByTerminalIdOrderByBusinessDateDescSaleTimeDesc(
            Long terminalId);

    List<Sale> findByAttendantIdOrderByBusinessDateDescSaleTimeDesc(
            Long attendantId);

    List<Sale> findByProductIdOrderByBusinessDateDescSaleTimeDesc(
            Long productId);

    List<Sale> findBySaleStatusOrderByBusinessDateDescSaleTimeDesc(
            SaleStatus saleStatus);

    List<Sale> findByPaymentStatusOrderByBusinessDateDescSaleTimeDesc(
            PaymentStatus paymentStatus);

    List<Sale> findByPaymentMethodOrderByBusinessDateDescSaleTimeDesc(
            PaymentMethod paymentMethod);

    long countByBusinessDate(LocalDate businessDate);

    long countBySaleStatus(SaleStatus saleStatus);

    long countByPaymentStatus(PaymentStatus paymentStatus);

    long countByPaymentMethod(PaymentMethod paymentMethod);

    boolean existsBySaleNumber(String saleNumber);

    boolean existsByReceiptNumber(String receiptNumber);

    boolean existsByTransactionReference(String transactionReference);

    @Query("""
       SELECT COALESCE(SUM(s.netAmount), 0)
       FROM Sale s
       WHERE s.businessDate = :businessDate
       """)
    BigDecimal totalSalesForDate(LocalDate businessDate);

    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM Sale s
        WHERE s.businessDate = :businessDate
        """)
    BigDecimal totalQuantityForDate(LocalDate businessDate);

    @Query("""
        SELECT COALESCE(SUM(s.netAmount), 0)
        FROM Sale s
        WHERE s.station.id = :stationId
        """)
    BigDecimal totalSalesByStation(Long stationId);

    @Query("""
        SELECT COALESCE(SUM(s.quantity), 0)
        FROM Sale s
        WHERE s.station.id = :stationId
        """)
    BigDecimal totalQuantityByStation(Long stationId);
    }
