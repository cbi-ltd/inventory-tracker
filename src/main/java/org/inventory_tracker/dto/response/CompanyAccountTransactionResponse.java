package org.inventory_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.enums.CompanyAccountTransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAccountTransactionResponse {

    private Long id;

    private Long agreementId;

    private Long saleId;

    private CompanyAccountTransactionType type;

    private BigDecimal amount;

    private LocalDateTime transactionDate;

    private String reference;

    private String description;
}
