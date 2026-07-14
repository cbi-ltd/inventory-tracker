package org.inventory_tracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.AssignPumpRequest;
import org.inventory_tracker.dto.request.ChangeTerminalAssignmentRequest;
import org.inventory_tracker.dto.response.PumpAssignmentResponse;
import org.inventory_tracker.service.PumpAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pump-assignments")
public class PumpAssignmentController {

    private final PumpAssignmentService pumpAssignmentService;


        @PatchMapping("/{assignmentId}/terminal")
        public ResponseEntity<ApiSuccessResponse<PumpAssignmentResponse>>changeTerminalAssignment(
                @PathVariable Long assignmentId,
                @Valid @RequestBody ChangeTerminalAssignmentRequest request) {

                PumpAssignmentResponse response =
                        pumpAssignmentService.changeTerminalAssignment(
                                assignmentId,
                                request);

                return ResponseEntity.ok(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.OK.value(),
                                "Assignment terminal updated successfully",
                                response
                        )
                );
        }


        @PostMapping
        public ResponseEntity<ApiSuccessResponse<PumpAssignmentResponse>>assignPumpToAttendant(
                @Valid @RequestBody AssignPumpRequest request) {

                PumpAssignmentResponse response =
                        pumpAssignmentService.assignPumpToAttendant(request);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(
                                new ApiSuccessResponse<>(
                                        LocalDateTime.now(),
                                        HttpStatus.CREATED.value(),
                                        "Pump assigned successfully",
                                        response
                                )
                        );
        }





        @GetMapping("/current/{attendantId}")
        public ResponseEntity<ApiSuccessResponse<PumpAssignmentResponse>> getPumpCurrentAssignment(
                        @PathVariable Long attendantId) {

                PumpAssignmentResponse response =
                        pumpAssignmentService
                                .getPumpCurrentAssignment(attendantId);

                return ResponseEntity.ok(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.OK.value(),
                                "Current pump assignment retrieved successfully",
                                response
                        )
                );
        }


        @GetMapping("/history/{attendantId}")
        public ResponseEntity<ApiSuccessResponse<List<PumpAssignmentResponse>>>getAttendantPumpAssignmentHistory(
                @PathVariable Long attendantId) {

                List<PumpAssignmentResponse> response =
                        pumpAssignmentService
                                .getAttendantPumpAssignmentHistory(attendantId);

                int count = response.size();

                return ResponseEntity.ok(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.OK.value(),
                                "Pump assignment history retrieved successfully",
                                count,
                                response
                        )
                );
        }


        @GetMapping("/today/{stationId}")
        public ResponseEntity<ApiSuccessResponse<List<PumpAssignmentResponse>>>getTodayPumpAssignments(
                @PathVariable Long stationId) {

                List<PumpAssignmentResponse> response =
                        pumpAssignmentService
                                .getTodayPumpAssignments(stationId);

                int count = response.size();

                return ResponseEntity.ok(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.OK.value(),
                                "Today's pump assignments retrieved successfully",
                                count,
                                response
                        )
                );
                }
        }

