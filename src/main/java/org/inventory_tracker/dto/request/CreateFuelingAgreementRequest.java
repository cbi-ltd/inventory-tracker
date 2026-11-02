package org.inventory_tracker.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.inventory_tracker.enums.FuelingSettlementType;
import org.inventory_tracker.util.ValidFuelingAgreementDates;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@ValidFuelingAgreementDates
public class CreateFuelingAgreementRequest {

    // @NotNull
    // private Long companyId;
    @NotNull
    @Valid
    private FuelingCompanyRequest company;

    private Long stationId;

    @NotNull
    private FuelingSettlementType settlementType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal prepaidBalance;

    private BigDecimal creditLimit;

    private String reference;

    private String remarks;
}
