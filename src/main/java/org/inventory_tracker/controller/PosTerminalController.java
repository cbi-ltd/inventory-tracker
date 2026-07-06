package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreatePosTerminalRequest;
import org.inventory_tracker.dto.response.PosTerminalResponse;
import org.inventory_tracker.service.PosTerminalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pos-terminals")
@RequiredArgsConstructor
public class PosTerminalController {

    private final PosTerminalService posTerminalService;

    @PostMapping
    public ResponseEntity<PosTerminalResponse> create(
            @RequestBody CreatePosTerminalRequest request
    ) {

        return new ResponseEntity<>(
                posTerminalService.create(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PosTerminalResponse>> getAll() {

        return ResponseEntity.ok(
                posTerminalService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PosTerminalResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                posTerminalService.getById(id)
        );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<PosTerminalResponse> update(
            @PathVariable Long id,
            @RequestBody CreatePosTerminalRequest request
    ) {

        return ResponseEntity.ok(
                posTerminalService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        posTerminalService.delete(id);

        return ResponseEntity.noContent().build();
    }
}