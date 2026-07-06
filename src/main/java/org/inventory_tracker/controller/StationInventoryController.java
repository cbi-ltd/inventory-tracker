package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.service.StationInventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station-inventories")
@RequiredArgsConstructor
public class StationInventoryController {

    private final StationInventoryService
            stationInventoryService;

    @PostMapping
    public ResponseEntity<StationInventoryResponse> create(
            @RequestBody CreateStationInventoryRequest request
    ) {

        return new ResponseEntity<>(
                stationInventoryService.create(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<StationInventoryResponse>>
    getAll() {

        return ResponseEntity.ok(
                stationInventoryService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationInventoryResponse>
    getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                stationInventoryService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationInventoryResponse>
    update(
            @PathVariable Long id,
            @RequestBody
            CreateStationInventoryRequest request
    ) {

        return ResponseEntity.ok(
                stationInventoryService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        stationInventoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}