package org.inventory_tracker.controller;



import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateFuelingAgreementRequest;
import org.inventory_tracker.dto.request.UpdateFuelingAgreementRequest;
import org.inventory_tracker.dto.response.CompanyAccountTransactionResponse;
import org.inventory_tracker.dto.response.FuelingAgreementBalanceResponse;
import org.inventory_tracker.dto.response.FuelingAgreementResponse;
import org.inventory_tracker.service.FuelingAgreementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/corporate-agreement")
@RequiredArgsConstructor
public class FuelingAgreementController {

    private final FuelingAgreementService fuelingAgreementService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    createAgreement(@Valid @RequestBody CreateFuelingAgreementRequest request) {
        FuelingAgreementResponse response = fuelingAgreementService.createAgreement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.CREATED.value(), "FuelingAgreement created successfully.", response));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<FuelingAgreementResponse>>>
    getAgreements() {

        List<FuelingAgreementResponse> response =
                fuelingAgreementService.getAgreements();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreements retrieved successfully.", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    getAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement retrieved successfully.", fuelingAgreementService.getAgreement(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    updateAgreement(@PathVariable Long id, @Valid @RequestBody UpdateFuelingAgreementRequest request) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement updated successfully.", fuelingAgreementService.updateAgreement(
                                id,
                                request)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    activateAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement activated successfully.", fuelingAgreementService.activateAgreement(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    deactivateAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement deactivated successfully.", fuelingAgreementService.deactivateAgreement(id)));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementBalanceResponse>>
    getBalance(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement balance retrieved successfully.", fuelingAgreementService.getAgreementBalance(id)));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiSuccessResponse<List<CompanyAccountTransactionResponse>>>
    getTransactions(@PathVariable Long id) {

        List<CompanyAccountTransactionResponse> response =
                fuelingAgreementService.getTransactions(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement transactions retrieved successfully.", response));
    }

    @GetMapping("/{companyId}/agreements")
    public ResponseEntity<ApiSuccessResponse<List<FuelingAgreementResponse>>>
    getCompanyAgreements(@PathVariable Long companyId) {

        List<FuelingAgreementResponse> response =
                fuelingAgreementService.getCompanyAgreements(companyId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Company agreements retrieved successfully.", response));
    }
}
