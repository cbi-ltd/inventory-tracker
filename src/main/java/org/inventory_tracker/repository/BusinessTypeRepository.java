package org.inventory_tracker.repository;

import org.inventory_tracker.entity.BusinessType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessTypeRepository extends JpaRepository<BusinessType, Long> {
}
