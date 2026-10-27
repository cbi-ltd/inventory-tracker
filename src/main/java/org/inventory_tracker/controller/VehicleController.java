package org.inventory_tracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.VehicleRequest;
import org.inventory_tracker.dto.response.VehicleResponse;
import org.inventory_tracker.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vehicles", description = "Vehicle management APIs")
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Create Vehicle", description = "This endpoint creates a new vehicle in the database.")
    @PostMapping
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> create(
            @Valid @RequestBody VehicleRequest request, @RequestParam Long companyId
    ) {
        VehicleResponse response = vehicleService.create(request, companyId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Vehicle created successfully",
                                response
                        )
                );
    }
    
    @Operation(summary = "Get All Vehicles", description = "This endpoint retrieves all vehicles from the database.")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<VehicleResponse>>> getAll() {

        List<VehicleResponse> response = vehicleService.getAll();

        return ResponseEntity.ok(
                new ApiSuccessResponse<List<VehicleResponse>>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicles retrieved successfully",
                        response.size(),
                        response
                )
        );
    }

    @Operation(summary = "Get Vehicle", description = "This endpoint retrieves a specific vehicle from the database.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> get(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicle retrieved successfully",
                        vehicleService.get(id)
                )
        );
    }

    @Operation(summary = "Get Vehicles by Company", description = "This endpoint retrieves all vehicles for a specific company from the database.")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiSuccessResponse<List<VehicleResponse>>> getByCompany(
            @PathVariable Long companyId
    ) {
        List<VehicleResponse> response =
                vehicleService.getByCompany(companyId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Company vehicles retrieved successfully",
                        response.size(),
                        response
                )
        );
    }

    @Operation(summary = "Update Vehicle", description = "This endpoint updates a specific vehicle in the database.")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicle updated successfully",
                        vehicleService.update(id, request)
                )
        );
    }

    @Operation(summary = "Activate Vehicle", description = "This endpoint activates a specific vehicle in the database.")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicle activated successfully",
                        vehicleService.activate(id)
                )
        );
    }

    @Operation(summary = "Deactivate Vehicle", description = "This endpoint deactivates a specific vehicle in the database.")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> deactivate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicle deactivated successfully",
                        vehicleService.deactivate(id)
                )
        );
    }
}
