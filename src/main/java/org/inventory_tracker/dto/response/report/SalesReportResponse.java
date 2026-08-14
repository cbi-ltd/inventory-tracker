package org.inventory_tracker.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.enums.PaymentStatus;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {

    private Long saleId;

    private String saleNumber;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpNumber;
    private String pumpName;

    private Long productId;
    private String productName;

    private Long attendantId;
    private String attendantName;

    private LocalDate businessDate;

    private LocalDateTime saleTime;

    private Shift shift;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal grossAmount;

    private BigDecimal discountAmount;

    private BigDecimal netAmount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private SaleStatus saleStatus;

    private String transactionReference;
}
