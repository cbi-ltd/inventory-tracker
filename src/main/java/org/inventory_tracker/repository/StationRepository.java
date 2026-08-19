package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.StationInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    long countByMerchant_CamsMerchantId(String merchantId);

    Optional<Station> findByIdAndMerchantId(Long stationId, Long MerchantId);

    List<Station> findByMerchant_CamsMerchantIdOrderByNameAsc(String camsMerchantId);

    List<Station> findByMerchant_IdAndActiveTrueOrderByNameAsc(Long merchantId);

    List<Station> findAllByMerchantId(Long merchantId);

    Optional<Station> findByIdAndMerchant_CamsMerchantId(Long id, String merchantId);

    boolean existsByIdAndMerchant_CamsMerchantId(Long id, String merchantId);

    boolean existsByIdAndMerchant_Id(Long id, Long merchantId);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    Optional<Station> findByCodeIgnoreCase(String code);

    Optional<Station> findByNameIgnoreCase(String name);

    List<Station> findAllByOrderByNameAsc();

    List<Station> findByActiveTrueOrderByNameAsc();
}
