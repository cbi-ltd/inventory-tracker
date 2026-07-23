package org.inventory_tracker.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.ProductPriceHistoryResponse;
import org.inventory_tracker.service.PriceHistoryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-price-history")
@RequiredArgsConstructor
public class ProductPriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getAllPriceHistory() {

        List<ProductPriceHistoryResponse> response =
                priceHistoryService.getAllPriceHistory();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product price history retrieved successfully",
                        count,
                        response
                )
        );
    }


    @GetMapping("/{historyId}")
    public ResponseEntity<ApiSuccessResponse<ProductPriceHistoryResponse>> getPriceHistoryById(@PathVariable Long historyId) {

        ProductPriceHistoryResponse response = priceHistoryService.getPriceHistoryById(historyId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product price history retrieved successfully",
                        1,
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryByStation(@PathVariable Long stationId) {

        List<ProductPriceHistoryResponse> response = priceHistoryService.getPriceHistoryByStation(stationId);

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station price history retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryByProduct(
            @PathVariable Long productId) {

        List<ProductPriceHistoryResponse> response = priceHistoryService.getPriceHistoryByProduct(productId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product price history retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/station/{stationId}/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryByStationAndProduct(
            @PathVariable Long stationId,
            @PathVariable Long productId) {

        List<ProductPriceHistoryResponse> response = priceHistoryService.getPriceHistoryByStationAndProduct(stationId, productId);

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station product price history retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/business-date")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryByBusinessDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate businessDate) {

        List<ProductPriceHistoryResponse> response =
                priceHistoryService.getPriceHistoryByBusinessDate(
                        businessDate);

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Business date price history retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/between-dates")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryBetweenDates(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        List<ProductPriceHistoryResponse> response = priceHistoryService.getPriceHistoryBetweenDates(startDate, endDate);

        int count = response.size();

        return ResponseEntity.ok(new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Price history retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/changed-by/{changedBy}")
    public ResponseEntity<ApiSuccessResponse<List<ProductPriceHistoryResponse>>> getPriceHistoryByChangedBy(
            @PathVariable String changedBy) {

        List<ProductPriceHistoryResponse> response = priceHistoryService.getPriceHistoryByChangedBy(changedBy);

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Price history retrieved successfully",
                        count,
                        response
                )
        );
    }
}
