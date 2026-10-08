package org.inventory_tracker.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.inventory_tracker.enums.FuelingSettlementType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdateFuelingAgreementRequest {

    private FuelingSettlementType settlementType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal creditLimit;

    private String reference;

    private String remarks;
}
