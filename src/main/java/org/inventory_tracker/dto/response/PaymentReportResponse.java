package org.inventory_tracker.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReportResponse {

    private Long paymentId;

    private Long saleId;
    private String saleNumber;

    private String transactionReference;

    private Long stationId;
    private String stationName;

    private Long pumpId;
    private String pumpNumber;

    private Long attendantId;
    private String attendantName;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDate businessDate;

    private LocalDateTime paymentTime;
}
