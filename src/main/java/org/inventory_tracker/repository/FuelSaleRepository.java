package org.inventory_tracker.repository;

import org.inventory_tracker.entity.FuelSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelSaleRepository
        extends JpaRepository<FuelSale, Long> {

    List<FuelSale> findByPumpAuditId(
            Long pumpAuditId
    );

    @Query("""
            SELECT COALESCE(
                SUM(f.litres),
                0
            )
            FROM FuelSale f
            WHERE f.pumpAudit.id = :pumpAuditId
            """)
    Double sumLitresByPumpAudit(
            Long pumpAuditId
    );

    @Query("""
            SELECT COALESCE(
                SUM(f.totalAmount),
                0
            )
            FROM FuelSale f
            WHERE f.pumpAudit.id = :pumpAuditId
            """)
    Double sumRevenueByPumpAudit(
            Long pumpAuditId
    );
}
