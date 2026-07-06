package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Pump;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PumpRepository extends JpaRepository<Pump, Long> {
}
