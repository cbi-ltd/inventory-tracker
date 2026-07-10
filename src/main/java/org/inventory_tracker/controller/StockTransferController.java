package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateStockTransferRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.StockTransferResponse;
import org.inventory_tracker.enums.StockTransferStatus;
import org.inventory_tracker.service.StockTransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock-transfers")
public class StockTransferController {

    private final StockTransferService stockTransferService;


    @PostMapping
    public ResponseEntity<ApiSuccessResponse<StockTransferResponse>>
    createTransfer(
            @Valid @RequestBody CreateStockTransferRequest request) {

        StockTransferResponse response =
                stockTransferService.createTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Stock transfer created successfully",
                                response
                        )
                );
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiSuccessResponse<StockTransferResponse>>
    completeTransfer(
            @PathVariable Long id) {

        StockTransferResponse response =
                stockTransferService.completeTransfer(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfer completed successfully",
                        response
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiSuccessResponse<StockTransferResponse>>
    cancelTransfer(
            @PathVariable Long id) {

        StockTransferResponse response =
                stockTransferService.cancelTransfer(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfer cancelled successfully",
                        response
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<StockTransferResponse>>
    getTransferById(
            @PathVariable Long id) {

        StockTransferResponse response =
                stockTransferService.getTransferById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfer retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/number/{transferNumber}")
    public ResponseEntity<ApiSuccessResponse<StockTransferResponse>>
    getTransferByTransferNumber(
            @PathVariable String transferNumber) {

        StockTransferResponse response =
                stockTransferService.getTransferByTransferNumber(
                        transferNumber
                );

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfer retrieved successfully",
                        response
                )
        );
    }

        @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getAllTransfers() {

        List<StockTransferResponse> response =
                stockTransferService.getAllTransfers();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersByStatus(
            @PathVariable StockTransferStatus status) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersByStatus(status);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersByProduct(
            @PathVariable Long productId) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersByProduct(productId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/source-station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersBySourceStation(
            @PathVariable Long stationId) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersBySourceStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Source station stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/destination-station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersByDestinationStation(
            @PathVariable Long stationId) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersByDestinationStation(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Destination station stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/date-range")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersBetweenDates(

            @RequestParam LocalDate startDate,

            @RequestParam LocalDate endDate) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersBetweenDates(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/source-inventory/{inventoryId}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersBySourceInventory(
            @PathVariable Long inventoryId) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersBySourceInventory(
                        inventoryId
                );

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Source inventory stock transfers retrieved successfully",
                        response
                )
        );
    }


    @GetMapping("/destination-inventory/{inventoryId}")
    public ResponseEntity<ApiSuccessResponse<List<StockTransferResponse>>>
    getTransfersByDestinationInventory(
            @PathVariable Long inventoryId) {

        List<StockTransferResponse> response =
                stockTransferService.getTransfersByDestinationInventory(
                        inventoryId
                );

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Destination inventory stock transfers retrieved successfully",
                        response
                )
        );
    }

}

