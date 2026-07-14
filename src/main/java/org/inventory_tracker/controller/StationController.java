package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.inventory_tracker.dto.request.UpdateStationRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StationResponse>> createStation(
            @Valid @RequestBody CreateStationRequest request) {

        StationResponse response =
                stationService.createStation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Station created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<StationResponse>>> getAllStations() {

        List<StationResponse> response =
                stationService.getAllStations();
        Integer count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stations retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StationResponse>> getStationById(
            @PathVariable Long id) {

        StationResponse response =
                stationService.getStationById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<StationResponse>>> getActiveStations() {

        List<StationResponse> response =
                stationService.getActiveStations();
        Integer count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active stations retrieved successfully",
                        count,
                        response
                )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StationResponse>> updateStation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStationRequest request) {

        StationResponse response =
                stationService.updateStation(id, request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<StationResponse>> activateStation(
            @PathVariable Long id) {

        StationResponse response =
                stationService.activateStation(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<StationResponse>> deactivateStation(
            @PathVariable Long id) {

        StationResponse response =
                stationService.deactivateStation(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station deactivated successfully",
                        response
                )
        );
    }
}
