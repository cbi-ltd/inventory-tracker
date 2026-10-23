package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {
    Optional<Terminal> findByIdAndActiveTrue(Long id);

    Optional<Terminal> findByTerminalSerialNumberAndStation_Merchant_Id(String serialNumber, Long merchantId);

    Optional<Terminal>findByTerminalSerialNumberAndActiveTrue(String terminalSerialNumber);

    Optional<Terminal> findByIdAndStation_Merchant_CamsMerchantId(Long terminalId, String camsMerchantId);

    List<Terminal>findByStation_Merchant_CamsMerchantIdAndActiveTrueOrderByTerminalSerialNumberAsc(String merchantId);
    
    List<Terminal>findByStation_Merchant_CamsMerchantIdOrderByTerminalSerialNumberAsc(String merchantId);

    Optional<Terminal>findByTerminalSerialNumberAndStation_Merchant_CamsMerchantId(String terminalSerialNumber, String merchantId);

    Optional<Terminal> findById(Long terminalId);

    Optional<Terminal> findByTerminalSerialNumber(String terminalSerialNumber);

    boolean existsByTerminalSerialNumber(String terminalSerialNumber);

    List<Terminal> findAllByOrderByTerminalSerialNumberAsc();

    List<Terminal> findByActiveTrueOrderByTerminalSerialNumberAsc();
}