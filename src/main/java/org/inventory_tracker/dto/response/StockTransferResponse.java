package org.inventory_tracker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.inventory_tracker.enums.StockTransferStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferResponse {

    private Long id;

    private String transferNumber;

    private Long sourceInventoryId;

    private Long destinationInventoryId;

    private Long sourceStationId;

    private String sourceStationName;

    private Long destinationStationId;

    private String destinationStationName;

    private Long productId;

    private String productName;

    private BigDecimal unitPrice;

    private BigDecimal quantityTransferred;

    private StockTransferStatus status;

    private String remarks;

    private LocalDate businessDate;

    private BigDecimal sourceBalanceBeforeTransfer;
    private BigDecimal sourceBalanceAfterTransfer;

    private BigDecimal destinationBalanceBeforeTransfer;
    private BigDecimal destinationBalanceAfterTransfer;

    private BigDecimal totalValueTransferred;

    private LocalDateTime completedAt;

    private String initiatedBy;

    private String completedBy;

}
