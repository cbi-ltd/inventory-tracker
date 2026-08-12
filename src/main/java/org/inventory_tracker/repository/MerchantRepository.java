package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface MerchantRepository
        extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByCamsMerchantId(String camsMerchantId);

    Optional<Merchant> findByMerchantEmail(String merchantEmail);

    boolean existsByCamsMerchantId(String camsMerchantId);
}
