package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    boolean existsByRole(org.inventory_tracker.enums.Role role);
}
