package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.inventory_tracker.service.PumpAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/pump-audits")
@RequiredArgsConstructor
public class PumpAuditController {

    private final PumpAuditService pumpAuditService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<PumpAuditResponse>> createPumpAudit(
            @Valid @RequestBody CreatePumpAuditRequest request) {

        PumpAuditResponse response =
                pumpAuditService.createPumpAudit(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Pump audit created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditResponse>>> getAllPumpAudits() {

        List<PumpAuditResponse> response =
                pumpAuditService.getAllPumpAudits();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump audits retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<PumpAuditResponse>> getPumpAuditById(
            @PathVariable Long id) {

        PumpAuditResponse response =
                pumpAuditService.getPumpAuditById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump audit retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<ApiSuccessResponse<PumpAuditResponse>> getPumpAuditByAssignment(
            @PathVariable Long assignmentId) {

        PumpAuditResponse response =
                pumpAuditService.getPumpAuditByAssignment(assignmentId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump audit retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditResponse>>> getPumpAuditsByStation(
            @PathVariable Long stationId) {

        List<PumpAuditResponse> response =
                pumpAuditService.getPumpAuditsByStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station pump audits retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/pump/{pumpId}")
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditResponse>>> getPumpAuditsByPump(
            @PathVariable Long pumpId) {

        List<PumpAuditResponse> response =
                pumpAuditService.getPumpAuditsByPump(pumpId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Pump audits retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/attendant/{attendantId}")
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditResponse>>> getPumpAuditsByAttendant(
            @PathVariable Long attendantId) {

        List<PumpAuditResponse> response =
                pumpAuditService.getPumpAuditsByAttendant(attendantId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Attendant pump audits retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/business-date/{businessDate}")
    public ResponseEntity<ApiSuccessResponse<List<PumpAuditResponse>>> getPumpAuditsByBusinessDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate businessDate) {

        List<PumpAuditResponse> response =
                pumpAuditService.getPumpAuditsByBusinessDate(businessDate);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Business date pump audits retrieved successfully",
                        response
                )
        );
    }
}