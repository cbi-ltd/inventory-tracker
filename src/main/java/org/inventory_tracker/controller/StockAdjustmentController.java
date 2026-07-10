package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStockAdjustmentRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.StockAdjustmentResponse;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.service.StockAdjustmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock-adjustments")
public class StockAdjustmentController {

    private final StockAdjustmentService stockAdjustmentService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StockAdjustmentResponse>>
    createAdjustment(
            @Valid @RequestBody CreateStockAdjustmentRequest request) {

        StockAdjustmentResponse response =
                stockAdjustmentService.createAdjustment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Stock adjustment created successfully.",
                                response
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StockAdjustmentResponse>>
    getAdjustmentById(
            @PathVariable Long id) {

        StockAdjustmentResponse response =
                stockAdjustmentService.getAdjustmentById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock adjustment retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAllAdjustments() {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAllAdjustments();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock adjustments retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAdjustmentsByStation(
            @PathVariable Long stationId) {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAdjustmentsByStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station stock adjustments retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAdjustmentsByProduct(
            @PathVariable Long productId) {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAdjustmentsByProduct(productId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product stock adjustments retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAdjustmentsByInventory(
            @PathVariable Long inventoryId) {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAdjustmentsByStationInventory(
                        inventoryId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory stock adjustments retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/reason/{reason}")
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAdjustmentsByReason(
            @PathVariable StockAdjustmentReason reason) {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAdjustmentsByReason(reason);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock adjustments retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiSuccessResponse<List<StockAdjustmentResponse>>>
    getAdjustmentsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<StockAdjustmentResponse> response =
                stockAdjustmentService.getAdjustmentsBetweenDates(
                        startDate,
                        endDate);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock adjustments retrieved successfully.",
                        response
                )
        );
    }
}
