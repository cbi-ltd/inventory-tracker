package org.inventory_tracker.repository;

import org.inventory_tracker.entity.Product;
import org.inventory_tracker.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Product> findAllByOrderByNameAsc();

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findByProductTypeOrderByNameAsc(
            ProductType productType
    );

    Optional<Product> findByNameIgnoreCase(String name);
}
