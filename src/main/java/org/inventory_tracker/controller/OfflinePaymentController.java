package org.inventory_tracker.controller;

import java.time.LocalDateTime;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.OfflinePaymentResponse;
import org.inventory_tracker.service.OfflinePaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/offline-payments")
@RequiredArgsConstructor
public class OfflinePaymentController {

    private final OfflinePaymentService offlinePaymentService;

    @PostMapping("/{id}/receive")
    public ResponseEntity<ApiSuccessResponse<OfflinePaymentResponse>>markOfflinePaymentAsReceived(
                @PathVariable Long id, @RequestParam String customerTransferReference) {

        OfflinePaymentResponse response = offlinePaymentService.markReceived(id, customerTransferReference);

        return ResponseEntity.ok(new ApiSuccessResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Offline payment marked as received",
                response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<OfflinePaymentResponse>>
    getOfflinePayment(@PathVariable Long id) {

        OfflinePaymentResponse response = offlinePaymentService.getOfflinePayment(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Offline payment retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<Page<OfflinePaymentResponse>>>
    getOfflinePayments(Pageable pageable) {

        Page<OfflinePaymentResponse> response = offlinePaymentService.getOfflinePayments(pageable);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Offline payments retrieved successfully.",
                        (int) response.getTotalElements(),
                        response
                )
        );
    }
}
