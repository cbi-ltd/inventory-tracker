package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.service.StationInventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.inventory_tracker.dto.request.UpdateStationInventoryRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;


@RestController
@RequestMapping("api/v1/station-inventories")
@RequiredArgsConstructor
public class StationInventoryController {

    private final StationInventoryService stationInventoryService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StationInventoryResponse>>
    createStationInventory(
            @Valid
            @RequestBody CreateStationInventoryRequest request) {

        StationInventoryResponse response =
                stationInventoryService
                        .createStationInventory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Station inventory created successfully",
                                response
                        )
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StationInventoryResponse>>
    updateStationInventory(
            @PathVariable Long id,
            @Valid
            @RequestBody UpdateStationInventoryRequest request) {

        StationInventoryResponse response =
                stationInventoryService
                        .updateStationInventory(id, request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station inventory updated successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StationInventoryResponse>>
    getInventoryById(@PathVariable Long id) {

        StationInventoryResponse response =
                stationInventoryService
                        .getInventoryById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory retrieved successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<StationInventoryResponse>>>
    getAllInventories() {

        List<StationInventoryResponse> response =
                stationInventoryService
                        .getAllInventories();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventories retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<StationInventoryResponse>>>
    getStationInventory(
            @PathVariable Long stationId) {

        List<StationInventoryResponse> response =
                stationInventoryService
                        .getStationInventory(stationId);

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station inventory retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<StationInventoryResponse>>>
    getActiveInventories() {

        List<StationInventoryResponse> response =
                stationInventoryService
                        .getActiveInventories();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active inventories retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<StationInventoryResponse>>
    activateInventory(@PathVariable Long id) {

        StationInventoryResponse response =
                stationInventoryService
                        .activateInventory(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<StationInventoryResponse>>
    deactivateInventory(@PathVariable Long id) {

        StationInventoryResponse response =
                stationInventoryService
                        .deactivateInventory(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory deactivated successfully",
                        response
                )
        );
    }
}