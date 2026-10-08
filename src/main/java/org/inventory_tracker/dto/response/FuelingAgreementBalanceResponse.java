package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
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
public class FuelingAgreementBalanceResponse {

    private Long agreementId;

    private FuelingSettlementType settlementType;

    private BigDecimal prepaidBalance;

    private BigDecimal outstandingBalance;

    private BigDecimal creditLimit;

    private BigDecimal availableBalance;

    private Boolean active;
}
