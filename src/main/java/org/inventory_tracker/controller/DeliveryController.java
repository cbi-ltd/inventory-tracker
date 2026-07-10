package org.inventory_tracker.controller;


import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateDeliveryRequest;
import org.inventory_tracker.dto.response.DeliveryResponse;
import org.inventory_tracker.enums.DeliveryStatus;
import org.inventory_tracker.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;


    @PostMapping
    public ResponseEntity<ApiSuccessResponse<DeliveryResponse>>
    createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {

        DeliveryResponse response =
                deliveryService.createDelivery(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Delivery created successfully.",
                                response
                        )
                );
    }


    @PatchMapping("/{id}/receive")
    public ResponseEntity<ApiSuccessResponse<DeliveryResponse>>
    receiveDelivery(
            @PathVariable Long id) {

        DeliveryResponse response =
                deliveryService.receiveDelivery(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Delivery received successfully.",
                        response
                )
        );
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiSuccessResponse<DeliveryResponse>>
    cancelDelivery(
            @PathVariable Long id) {

        DeliveryResponse response =
                deliveryService.cancelDelivery(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Delivery cancelled successfully.",
                        response
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<DeliveryResponse>>
    getDeliveryById(
            @PathVariable Long id) {

        DeliveryResponse response =
                deliveryService.getDeliveryById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Delivery retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/number/{deliveryNumber}")
    public ResponseEntity<ApiSuccessResponse<DeliveryResponse>>
    getDeliveryByDeliveryNumber(
            @PathVariable String deliveryNumber) {

        DeliveryResponse response =
                deliveryService.getDeliveryByDeliveryNumber(
                        deliveryNumber);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Delivery retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getAllDeliveries() {

        List<DeliveryResponse> response =
                deliveryService.getAllDeliveries();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getStationDeliveries(
            @PathVariable Long stationId) {

        List<DeliveryResponse> response =
                deliveryService.getStationDeliveries(stationId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getProductDeliveries(
            @PathVariable Long productId) {

        List<DeliveryResponse> response =
                deliveryService.getProductDeliveries(productId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/inventory/{stationInventoryId}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getInventoryDeliveries(
            @PathVariable Long stationInventoryId) {

        List<DeliveryResponse> response =
                deliveryService.getInventoryDeliveries(
                        stationInventoryId);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Inventory deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getDeliveriesByStatus(
            @PathVariable DeliveryStatus status) {

        List<DeliveryResponse> response =
                deliveryService.getDeliveriesByStatus(status);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/date-range")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getDeliveriesBetweenDates(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        List<DeliveryResponse> response =
                deliveryService.getDeliveriesBetweenDates(
                        startDate,
                        endDate);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Deliveries retrieved successfully.",
                        response
                )
        );
    }


    @GetMapping("/station/{stationId}/date-range")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getStationDeliveriesBetweenDates(

            @PathVariable Long stationId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        List<DeliveryResponse> response =
                deliveryService.getStationDeliveriesBetweenDates(
                        stationId,
                        startDate,
                        endDate);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station deliveries retrieved successfully.",
                        response
                )
        );
    }

}
