package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station,Long> {
}
