package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PosTerminal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosTerminalRepository extends JpaRepository<PosTerminal, Long> {

    boolean existsBySerialNumber(String serialNumber);
    boolean existsByMacAddress(String macAddress);
}