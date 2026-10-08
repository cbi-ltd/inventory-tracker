package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.enums.FuelingSettlementType;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelingAgreementResponse {

    private Long id;

    private Long companyId;
    private String companyName;

    private Long merchantId;

    private FuelingSettlementType settlementType;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal prepaidBalance;
    private BigDecimal outstandingBalance;
    private BigDecimal creditLimit;

    private Boolean active;

    private String reference;
    private String remarks;
}
