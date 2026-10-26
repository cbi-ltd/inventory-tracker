package org.inventory_tracker.controller;



import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateFuelingAgreementRequest;
import org.inventory_tracker.dto.request.UpdateFuelingAgreementRequest;
import org.inventory_tracker.dto.response.CompanyAccountTransactionResponse;
import org.inventory_tracker.dto.response.FuelingAgreementBalanceResponse;
import org.inventory_tracker.dto.response.FuelingAgreementResponse;
import org.inventory_tracker.dto.response.FuelingCompanyResponse;
import org.inventory_tracker.enums.FuelingSettlementType;
import org.inventory_tracker.service.FuelingAgreementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;



@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Corporate Agreements", description = "Corporate agreement management APIs")
@RequestMapping("/api/v1/corporate-agreement")
@RequiredArgsConstructor
public class FuelingAgreementController {

    private final FuelingAgreementService fuelingAgreementService;

    @Operation(summary = "Create Agreement", description = "This endpoint accepts agreement details, validates them, and saves them to the database.")
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    createAgreement(@Valid @RequestBody CreateFuelingAgreementRequest request) {
        FuelingAgreementResponse response = fuelingAgreementService.createAgreement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.CREATED.value(), "FuelingAgreement created successfully.", response));
    }

    @Operation(summary = "Get Agreements", description = "This endpoint retrieves all agreements from the database.")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<FuelingAgreementResponse>>>
    getAgreements() {

        List<FuelingAgreementResponse> response = fuelingAgreementService.getAgreements();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreements retrieved successfully.", response));
    }

    @Operation(summary = "Get Agreement", description = "This endpoint retrieves a specific agreement from the database.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    getAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement retrieved successfully.", fuelingAgreementService.getAgreement(id)));
    }

    @Operation(summary = "Get POS Agreement", description = "Retrieves a fueling agreement by ID for the specified POS terminal.")
    @GetMapping("/pos/{agreementId}")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    getPosAgreement(@RequestParam String terminalSerialNumber, @PathVariable Long agreementId) {

        FuelingAgreementResponse response = fuelingAgreementService.getAgreementForTerminal(terminalSerialNumber, agreementId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Agreement retrieved successfully.",
                        response
                )
        );
    }


    @Operation(summary = "Search POS Agreements by Company Name", description = "Searches fueling agreements by a case-insensitive, partial company name for the specified POS terminal.")
    @GetMapping("/pos/search")
    public ResponseEntity<ApiSuccessResponse<List<FuelingAgreementResponse>>>
    getPosAgreementsByCompanyName(@RequestParam String terminalSerialNumber, @RequestParam String companyName) {

        List<FuelingAgreementResponse> response =
                fuelingAgreementService.getAgreementsForTerminalByCompanyName(terminalSerialNumber, companyName);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Agreements retrieved successfully.",
                        response
                )
        );
    }


    @Operation(summary = "Get POS Fueling Companies by Settlement Type", description = "Retrieves fueling companies with active, currently valid agreements of the specified settlement type.")
    @GetMapping("/pos/fueling-companies")
    public ResponseEntity<ApiSuccessResponse<List<FuelingCompanyResponse>>>
    getPosFuelingCompaniesBySettlementType(
            @RequestParam String terminalSerialNumber,
            @RequestParam FuelingSettlementType settlementType) {

        List<FuelingCompanyResponse> response = fuelingAgreementService.getPosFuelingCompaniesBySettlementType(terminalSerialNumber, settlementType);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling companies retrieved successfully.",
                        response
                )
        );
    }

    @Operation(summary = "Update Agreement", description = "This endpoint updates a specific agreement in the database.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    updateAgreement(@PathVariable Long id, @Valid @RequestBody UpdateFuelingAgreementRequest request) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement updated successfully.", fuelingAgreementService.updateAgreement(
                                id,
                                request)));
    }

    @Operation(summary = "Activate Agreement", description = "This endpoint activates a specific agreement in the database.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    activateAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement activated successfully.", fuelingAgreementService.activateAgreement(id)));
    }

    @Operation(summary = "Deactivate Agreement", description = "This endpoint deactivates a specific agreement in the database.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementResponse>>
    deactivateAgreement(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement deactivated successfully.", fuelingAgreementService.deactivateAgreement(id)));
    }

    @Operation(summary = "Get Agreement Balance", description = "This endpoint retrieves the balance of a specific agreement from the database.")
    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiSuccessResponse<FuelingAgreementBalanceResponse>>
    getBalance(@PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement balance retrieved successfully.", fuelingAgreementService.getAgreementBalance(id)));
    }

    @Operation(summary = "Get Agreement Transactions", description = "This endpoint retrieves the transactions for a specific agreement from the database.")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiSuccessResponse<List<CompanyAccountTransactionResponse>>>
    getTransactions(@PathVariable Long id) {

        List<CompanyAccountTransactionResponse> response =
                fuelingAgreementService.getTransactions(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Agreement transactions retrieved successfully.", response));
    }

    @Operation(summary = "Get Company Agreements", description = "This endpoint retrieves all agreements for a specific company from the database.")
    @GetMapping("/{companyId}/agreements")
    public ResponseEntity<ApiSuccessResponse<List<FuelingAgreementResponse>>>
    getCompanyAgreements(@PathVariable Long companyId) {

        List<FuelingAgreementResponse> response = fuelingAgreementService.getCompanyAgreements(companyId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Company agreements retrieved successfully.", response));
    }
}
