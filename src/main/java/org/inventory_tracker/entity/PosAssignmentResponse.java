package org.inventory_tracker.entity;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosAssignmentResponse {

    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    private Long productId;
    private String productName;

    private Long attendantId;
    private String attendantName;

    private BigDecimal openingReading;
    private BigDecimal closingReading;

    private Long pumpAuditId;
}
