package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreatePumpAuditRequest;
import org.inventory_tracker.dto.response.PumpAuditResponse;
import org.inventory_tracker.service.PumpAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pump-audits")
@RequiredArgsConstructor
public class PumpAuditController {

    private final PumpAuditService pumpAuditService;

    @PostMapping
    public ResponseEntity<PumpAuditResponse> create(
            @RequestBody CreatePumpAuditRequest request
    ) {

        return new ResponseEntity<>(
                pumpAuditService.create(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PumpAuditResponse>>
    getAll() {

        return ResponseEntity.ok(
                pumpAuditService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PumpAuditResponse>
    getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                pumpAuditService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PumpAuditResponse>
    update(
            @PathVariable Long id,
            @RequestBody CreatePumpAuditRequest request
    ) {

        return ResponseEntity.ok(
                pumpAuditService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        pumpAuditService.delete(id);

        return ResponseEntity.noContent().build();
    }
}