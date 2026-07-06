package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationRequest;
import org.inventory_tracker.dto.response.StationResponse;
import org.inventory_tracker.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<StationResponse> createStation(@Valid @RequestBody CreateStationRequest request) {
        StationResponse response = stationService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
