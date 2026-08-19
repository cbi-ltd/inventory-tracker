package org.inventory_tracker.repository;


import org.inventory_tracker.entity.Payment;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    List<Payment> findBySale_Station_Merchant_CamsMerchantId(String camsMerchantId);
    
    List<Payment> findBySale_Station_Merchant_IdAndPaymentMethodOrderByPaymentTimeDesc(Long merchantId, PaymentMethod paymentMethod);
    
    List<Payment> findBySale_Station_Merchant_IdAndPaymentStatusOrderByPaymentTimeDesc(Long merchantId, PaymentStatus paymentStatus);

    @Query("""
    SELECT p
    FROM Payment p
    JOIN p.sale s
    JOIN s.station st
    WHERE st.merchant.id = :merchantId
    ORDER BY p.paymentTime DESC
    """)
    List<Payment> findAllByMerchantIdOrderByPaymentTimeDesc(Long merchantId);

    Optional<Payment> findByGatewayReference(String gatewayReference);
    
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<Payment> findBySaleId(Long saleId);

    Optional<Payment> findByRrn(String rrn);

    Optional<Payment> findByStan(String stan);

    List<Payment> findAllByOrderByPaymentTimeDesc();

    List<Payment> findByPaymentStatusOrderByPaymentTimeDesc(PaymentStatus paymentStatus);

    List<Payment> findByPaymentMethodOrderByPaymentTimeDesc(PaymentMethod paymentMethod);

    List<Payment> findByPaymentTimeBetweenOrderByPaymentTimeDesc(LocalDateTime start, LocalDateTime end);

    List<Payment> findByTerminalIdOrderByPaymentTimeDesc(Long terminalId);

    long countByPaymentStatus(PaymentStatus paymentStatus);

    long countByPaymentMethod(PaymentMethod paymentMethod);

    boolean existsByTransactionReference(String transactionReference);

    boolean existsByPaymentNumber(String paymentNumber);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = :status")
    BigDecimal totalAmountByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentMethod = :method")
    BigDecimal totalAmountByMethod(PaymentMethod method);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.terminal.id = :terminalId")
    BigDecimal totalAmountByTerminal(Long terminalId);
}
