package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    Optional<Terminal> findByTerminalId(String terminalId);

    Optional<Terminal> findByTerminalSerialNumber(
            String terminalSerialNumber
    );

    boolean existsByTerminalId(String terminalId);

    boolean existsByTerminalSerialNumber(
            String terminalSerialNumber
    );


    List<Terminal> findAllByOrderByTerminalIdAsc();

    List<Terminal> findByActiveTrueOrderByTerminalIdAsc();
}