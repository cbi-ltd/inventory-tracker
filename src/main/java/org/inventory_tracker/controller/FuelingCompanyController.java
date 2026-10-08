package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.FuelingCompanyRequest;
import org.inventory_tracker.dto.response.FuelingCompanyResponse;
import org.inventory_tracker.service.FuelingCompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/v1/fueling-company")
@RequiredArgsConstructor
public class FuelingCompanyController {

    private final FuelingCompanyService fuelingCompanyService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> create(
            @Valid @RequestBody FuelingCompanyRequest request
    ) {
        FuelingCompanyResponse response = fuelingCompanyService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.CREATED.value(),
                        "Fueling company created successfully",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<FuelingCompanyResponse>>> getAll() {

        List<FuelingCompanyResponse> response = fuelingCompanyService.getAll();

        return ResponseEntity.ok(   
                new ApiSuccessResponse<List<FuelingCompanyResponse>>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling companies retrieved successfully",
                        response.size(),
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> get(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company retrieved successfully",
                        fuelingCompanyService.get(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelingCompanyRequest request
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company updated successfully",
                        fuelingCompanyService.update(id, request)
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> activate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company activated successfully",
                        fuelingCompanyService.activate(id)
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<FuelingCompanyResponse>> deactivate(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Fueling company deactivated successfully",
                        fuelingCompanyService.deactivate(id)
                )
        );
    }
}
