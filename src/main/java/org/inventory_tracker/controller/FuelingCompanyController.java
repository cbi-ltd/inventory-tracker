package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.FuelingCompanyRequest;
import org.inventory_tracker.dto.response.FuelingCompanyResponse;
import org.inventory_tracker.service.FuelingCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fueling Companies", description = "Fueling company management APIs")
@RequestMapping("/api/v1/fueling-company")
@RequiredArgsConstructor
public class FuelingCompanyController {

    private final FuelingCompanyService fuelingCompanyService;

    @Operation(summary = "Create Fueling Company", description = "This endpoint creates a new fueling company in the database.")
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> create(
            @Valid @RequestBody FuelingCompanyRequest request
    ) {
        FuelingCompanyResponse response = fuelingCompanyService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Fueling company created successfully",
                        response
                ));
    }

    @Operation(summary = "Get All Fueling Companies", description = "This endpoint retrieves all fueling companies from the database.")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<FuelingCompanyResponse>>> getAll() {

        List<FuelingCompanyResponse> response = fuelingCompanyService.getAll();

        return ResponseEntity.ok(   
                new ApiSuccessResponse<List<FuelingCompanyResponse>>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling companies retrieved successfully",
                        response.size(),
                        response
                )
        );
    }
    
    @Operation(summary = "Get Fueling Company", description = "This endpoint retrieves a specific fueling company from the database.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> get(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company retrieved successfully",
                        fuelingCompanyService.get(id)
                )
        );
    }

    @Operation(summary = "Update Fueling Company", description = "This endpoint updates a specific fueling company in the database.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelingCompanyRequest request
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company updated successfully",
                        fuelingCompanyService.update(id, request)
                )
        );
    }

    @Operation(summary = "Activate Fueling Company", description = "This endpoint activates a specific fueling company in the database.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> activate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company activated successfully",
                        fuelingCompanyService.activate(id)
                )
        );
    }

    @Operation(summary = "Deactivate Fueling Company", description = "This endpoint deactivates a specific fueling company in the database.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> deactivate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company deactivated successfully",
                        fuelingCompanyService.deactivate(id)
                )
        );
    }
}
