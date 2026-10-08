package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.inventory_tracker.enums.FuelingSettlementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuelingAgreementSummaryResponse {

    private Long id;
    private String reference;
    private FuelingSettlementType settlementType;
    private Long companyId;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal creditLimit;
    private BigDecimal outstandingBalance;
    private BigDecimal prepaidBalance;
}
