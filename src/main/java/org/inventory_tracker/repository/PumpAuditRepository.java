package org.inventory_tracker.repository;

import org.inventory_tracker.entity.PumpAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PumpAuditRepository extends JpaRepository<PumpAudit,Long> {
}
