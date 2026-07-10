package org.inventory_tracker.controller;

import org.inventory_tracker.dto.request.CreatePumpRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.PumpResponse;
import org.inventory_tracker.service.PumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pumps")
@RequiredArgsConstructor
public class PumpController {

    private final PumpService pumpService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<PumpResponse>> createPump(
            @Valid @RequestBody CreatePumpRequest request) {

        PumpResponse response =
                pumpService.createPump(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Pump created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<PumpResponse>>> getAllPumps() {

        List<PumpResponse> response =
                pumpService.getAllPumps();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pumps retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<PumpResponse>> getPumpById(
            @PathVariable Long id) {

        PumpResponse response =
                pumpService.getPumpById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<PumpResponse>>> getPumpsByStation(
            @PathVariable Long stationId) {

        List<PumpResponse> response =
                pumpService.getPumpsByStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station pumps retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<PumpResponse>>> getActivePumps() {

        List<PumpResponse> response =
                pumpService.getActivePumps();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active pumps retrieved successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<PumpResponse>> updatePump(
            @PathVariable Long id,
            @Valid @RequestBody CreatePumpRequest request) {

        PumpResponse response =
                pumpService.updatePump(id, request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<PumpResponse>> activatePump(
            @PathVariable Long id) {

        PumpResponse response =
                pumpService.activatePump(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<PumpResponse>> deactivatePump(
            @PathVariable Long id) {

        PumpResponse response =
                pumpService.deactivatePump(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump deactivated successfully",
                        response
                )
        );
    }
}
