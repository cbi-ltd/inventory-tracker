package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateAttendantRequest;
import org.inventory_tracker.dto.response.AttendantResponse;
import org.inventory_tracker.service.AttendantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendants")
@RequiredArgsConstructor
public class AttendantController {

    private final AttendantService attendantService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<AttendantResponse>> createAttendant(
            @Valid @RequestBody CreateAttendantRequest request) {

        AttendantResponse response =
                attendantService.createAttendant(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Attendant created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<AttendantResponse>>> getAllAttendants() {

        List<AttendantResponse> response =
                attendantService.getAllAttendants();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendants retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<AttendantResponse>> getAttendantById(
        @PathVariable Long id) {

        AttendantResponse response =
                attendantService.getAttendantById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant retrieved successfully",
                        response
                )
        );
     }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<AttendantResponse>>> getAttendantsByStation(
        @PathVariable Long stationId) {

        List<AttendantResponse> response =
                attendantService.getAttendantsByStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station attendants retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<AttendantResponse>>> getActiveAttendants() {

        List<AttendantResponse> response =
                attendantService.getActiveAttendants();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active attendants retrieved successfully",
                        response
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<AttendantResponse>> updateAttendant(
        @PathVariable Long id,
        @Valid @RequestBody CreateAttendantRequest request) {

        AttendantResponse response =
                attendantService.updateAttendant(id, request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<AttendantResponse>> activateAttendant(
        @PathVariable Long id) {

        AttendantResponse response =
                attendantService.activateAttendant(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant activated successfully",
                        response
                )
        );
    }


    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<AttendantResponse>> deactivateAttendant(
            @PathVariable Long id) {

        AttendantResponse response =
                attendantService.deactivateAttendant(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant deactivated successfully",
                        response
                )
        );
    }
}

