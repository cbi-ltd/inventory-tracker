package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Long> {

    Optional<Terminal> findByTid(String tid);

    Optional<Terminal> findByTerminalSerialNumber(
            String terminalSerialNumber
    );

    boolean existsByTid(String tid);

    boolean existsByTerminalSerialNumber(
            String terminalSerialNumber
    );


    List<Terminal> findAllByOrderByTidAsc();

    List<Terminal> findByActiveTrueOrderByTidAsc();
}