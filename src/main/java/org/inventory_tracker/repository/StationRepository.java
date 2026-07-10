package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface StationRepository
        extends JpaRepository<Station, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    Optional<Station> findByCodeIgnoreCase(String code);

    Optional<Station> findByNameIgnoreCase(String name);

    List<Station> findAllByOrderByNameAsc();

    List<Station> findByActiveTrueOrderByNameAsc();
}
