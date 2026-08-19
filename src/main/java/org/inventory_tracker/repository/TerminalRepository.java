package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
    Optional<Terminal>findByTerminalSerialNumberAndActiveTrue(String terminalSerialNumber);

    Optional<Terminal> findByIdAndStation_Merchant_CamsMerchantId(
            Long terminalId,
            String camsMerchantId
    );

    List<Terminal>findByStation_Merchant_CamsMerchantIdAndActiveTrueOrderByTidAsc(String merchantId);
    
    List<Terminal>findByStation_Merchant_CamsMerchantIdOrderByTidAsc(String merchantId);

    Optional<Terminal>findByTidAndStation_Merchant_CamsMerchantId(String tid, String merchantId);

    Optional<Terminal>findByTerminalSerialNumberAndStation_Merchant_CamsMerchantId(String terminalSerialNumber, String merchantId);

    Optional<Terminal> findByTid(String tid);

    Optional<Terminal> findByTerminalSerialNumber(String terminalSerialNumber);

    boolean existsByTid(String tid);

    boolean existsByTerminalSerialNumber(String terminalSerialNumber);

    List<Terminal> findAllByOrderByTidAsc();

    List<Terminal> findByActiveTrueOrderByTidAsc();
}