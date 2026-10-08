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
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> create(
            @Valid @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehicleService.create(request);
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

    @PutMapping("/{id}")
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

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<VehicleResponse>> activate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Vehicle activated successfully",
                        vehicleService.activate(id)
                )
        );
    }

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
