package org.inventory_tracker.controller;

import org.inventory_tracker.dto.request.CreateReconciliationRequest;
import org.inventory_tracker.dto.response.OfflinePaymentReconciliationResponse;
import org.inventory_tracker.service.OfflinePaymentReconciliationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/offline-reconciliations")
@RequiredArgsConstructor
@Validated
public class OfflinePaymentReconciliationController {

    private final OfflinePaymentReconciliationService reconciliationService;

    @PostMapping
    public ResponseEntity<OfflinePaymentReconciliationResponse>
    createReconciliation(@Valid @RequestBody CreateReconciliationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reconciliationService.createReconciliation(request));
    }

    @GetMapping
    public ResponseEntity<Page<OfflinePaymentReconciliationResponse>>
    getReconciliations(
            @PageableDefault(size = 20, sort = "initiatedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(reconciliationService.getReconciliations(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfflinePaymentReconciliationResponse>
    getReconciliation(@PathVariable Long id) {
        return ResponseEntity.ok(reconciliationService.getReconciliation(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OfflinePaymentReconciliationResponse>
    confirmReconciliation(@PathVariable Long id) {
        return ResponseEntity.ok(reconciliationService.confirmReconciliation(id));
    }

    @PostMapping("/{id}/fail")
    public ResponseEntity<OfflinePaymentReconciliationResponse>
    failReconciliation(@PathVariable Long id, @RequestParam String failureReason) {
        return ResponseEntity.ok(reconciliationService.failReconciliation(id, failureReason));
    }
}
