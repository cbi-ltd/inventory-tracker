package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStockCountRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.StockCountResponse;
import org.inventory_tracker.service.StockCountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock-counts")
public class StockCountController {

    private final StockCountService stockCountService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StockCountResponse>>
    createStockCount(
            @Valid @RequestBody CreateStockCountRequest request) {

        StockCountResponse response =
                stockCountService.createStockCount(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Stock count created successfully.",
                                response
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StockCountResponse>>
    getStockCountById(
            @PathVariable Long id) {

        StockCountResponse response =
                stockCountService.getStockCountById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock count retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<StockCountResponse>>>
    getAllStockCounts() {

        List<StockCountResponse> response =
                stockCountService.getAllStockCounts();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock counts retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<StockCountResponse>>>
    getStockCountsByStation(
            @PathVariable Long stationId) {

        List<StockCountResponse> response =
                stockCountService.getStockCountsByStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station stock counts retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<StockCountResponse>>>
    getStockCountsByProduct(
            @PathVariable Long productId) {

        List<StockCountResponse> response =
                stockCountService.getStockCountsByProduct(productId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product stock counts retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/inventory/{stationInventoryId}")
    public ResponseEntity<ApiSuccessResponse<List<StockCountResponse>>>
    getStockCountsByStationInventory(
            @PathVariable Long stationInventoryId) {

        List<StockCountResponse> response =
                stockCountService.getStockCountsByStationInventory(
                        stationInventoryId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory stock counts retrieved successfully.",
                        response
                )
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiSuccessResponse<List<StockCountResponse>>>
    getStockCountsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<StockCountResponse> response =
                stockCountService.getStockCountsBetweenDates(
                        startDate,
                        endDate);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock counts retrieved successfully.",
                        response
                )
        );
    }
}
