package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.service.PaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.PaymentFilterRequest;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<PaymentResponse>>> filterPayments(
        @RequestParam(required = false) Long stationId, @RequestParam(required = false) Long terminalId,
        @RequestParam(required = false) Long saleId, @RequestParam(required = false) PaymentStatus paymentStatus,
        @RequestParam(required = false) PaymentMethod paymentMethod, @RequestParam(required = false) String paymentNumber,
        @RequestParam(required = false) String transactionReference, @RequestParam(required = false) String gatewayReference,
        @RequestParam(required = false) String processor, @RequestParam(required = false) String payerName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) BigDecimal minAmount, @RequestParam(required = false) BigDecimal maxAmount) {

        PaymentFilterRequest request = new PaymentFilterRequest();

        request.setStationId(stationId);
        request.setTerminalId(terminalId);
        request.setSaleId(saleId);
        request.setPaymentStatus(paymentStatus);
        request.setPaymentMethod(paymentMethod);
        request.setPaymentNumber(paymentNumber);
        request.setTransactionReference(transactionReference);
        request.setGatewayReference(gatewayReference);
        request.setProcessor(processor);
        request.setPayerName(payerName);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setMinAmount(minAmount);
        request.setMaxAmount(maxAmount);

        List<PaymentResponse> response = paymentService.filterPayments(request);
        int paymentCount = response.size();
        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                                 "Payments retrieved successfully.", paymentCount, response));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/number/{paymentNumber}")
    public PaymentResponse getPaymentByPaymentNumber(@PathVariable String paymentNumber) {
        return paymentService.getPaymentByPaymentNumber(paymentNumber);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/transaction/{transactionReference}")
    public PaymentResponse getPaymentByTransactionReference(@PathVariable String transactionReference) {
        return paymentService.getPaymentByTransactionReference(transactionReference);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/sale/{saleId}")
    public PaymentResponse getPaymentBySale(@PathVariable Long saleId) {
        return paymentService.getPaymentBySale(saleId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/status/{status}")
    public List<PaymentResponse> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/method/{method}")
    public List<PaymentResponse> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        return paymentService.getPaymentsByMethod(method);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{paymentId}/cancel")
    public PaymentResponse cancelPayment(@PathVariable Long paymentId) {
        return paymentService.cancelPayment(paymentId);
    }

}
