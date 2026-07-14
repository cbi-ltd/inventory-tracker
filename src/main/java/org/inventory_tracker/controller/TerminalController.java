package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.TerminalSyncRequest;
import org.inventory_tracker.dto.response.TerminalResponse;
import org.inventory_tracker.service.TerminalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/terminals")
@RequiredArgsConstructor
public class TerminalController {

    private final TerminalService terminalService;

    @PostMapping("/sync")
    public ResponseEntity<ApiSuccessResponse<TerminalResponse>>
    syncTerminal(
            @Valid @RequestBody TerminalSyncRequest request) {

        TerminalResponse response =
                terminalService.syncTerminal(request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Terminal synchronized successfully",
                        response
                )
        );
    }

    @GetMapping("/{terminalId}")
    public ResponseEntity<ApiSuccessResponse<TerminalResponse>>
    getTerminalByTerminalId(
            @PathVariable String terminalId) {

        TerminalResponse response =
                terminalService.getTerminalByTerminalId(
                        terminalId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Terminal retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/serial/{terminalSerialNumber}")
    public ResponseEntity<ApiSuccessResponse<TerminalResponse>>
    getTerminalByTerminalSerialNumber(
            @PathVariable String terminalSerialNumber) {

        TerminalResponse response =
                terminalService.getTerminalByTerminalSerialNumber(
                        terminalSerialNumber);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Terminal retrieved successfully",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<TerminalResponse>>>
    getAllTerminals() {

        List<TerminalResponse> response =
                terminalService.getAllTerminals();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Terminals retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<TerminalResponse>>>
    getActiveTerminals() {

        List<TerminalResponse> response =
                terminalService.getActiveTerminals();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active terminals retrieved successfully",
                        count,
                        response
                )
        );
    }

}