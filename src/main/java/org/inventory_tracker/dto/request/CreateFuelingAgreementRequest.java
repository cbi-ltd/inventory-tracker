package org.inventory_tracker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.FuelingSettlementType;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
public class CreateFuelingAgreementRequest {

    @NotNull
    private Long companyId;

    @NotNull
    private FuelingSettlementType settlementType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private BigDecimal prepaidBalance;

    private BigDecimal creditLimit;

    private String reference;

    private String remarks;
}
