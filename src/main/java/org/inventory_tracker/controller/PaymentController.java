package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/number/{paymentNumber}")
    public PaymentResponse getPaymentByPaymentNumber(@PathVariable String paymentNumber) {
        return paymentService.getPaymentByPaymentNumber(paymentNumber);
    }

    @GetMapping("/transaction/{transactionReference}")
    public PaymentResponse getPaymentByTransactionReference(@PathVariable String transactionReference) {
        return paymentService.getPaymentByTransactionReference(transactionReference);
    }

    @GetMapping("/sale/{saleId}")
    public PaymentResponse getPaymentBySale(@PathVariable Long saleId) {
        return paymentService.getPaymentBySale(saleId);
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/status/{status}")
    public List<PaymentResponse> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status);
    }

    @GetMapping("/method/{method}")
    public List<PaymentResponse> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        return paymentService.getPaymentsByMethod(method);
    }

    @PutMapping("/{paymentId}/cancel")
    public PaymentResponse cancelPayment(@PathVariable Long paymentId) {
        return paymentService.cancelPayment(paymentId);
    }

    // @PutMapping("/status")
    // public PaymentResponse updatePaymentStatus(@RequestParam String transactionReference,
    //         @RequestParam PaymentStatus paymentStatus, @RequestParam(required = false) String responseCode,
    //         @RequestParam(required = false) String responseMessage) {

    //     return paymentService.updatePaymentStatus(transactionReference, paymentStatus, responseCode, responseMessage);
    // }

}
