package org.inventory_tracker.repository;

import org.inventory_tracker.entity.CompanyAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;   


public interface CompanyAccountTransactionRepository extends JpaRepository<CompanyAccountTransaction, Long> {
    
    List<CompanyAccountTransaction>findByFuelingAgreement_IdOrderByTransactionDateDesc(Long agreementId);
}
