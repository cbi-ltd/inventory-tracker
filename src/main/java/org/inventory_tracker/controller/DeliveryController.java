package org.inventory_tracker.controller;


import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateDeliveryRequest;
import org.inventory_tracker.dto.request.DeliveryFilterRequest;
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

//     @GetMapping
//     public List<DeliveryResponse> filterDeliveries(

//             @RequestParam(required = false)
//             String deliveryNumber,

//             @RequestParam(required = false)
//             Long stationId,

//             @RequestParam(required = false)
//             Long productId,

//             @RequestParam(required = false)
//             Long stationInventoryId,

//             @RequestParam(required = false)
//             DeliveryStatus status,

//             @RequestParam(required = false)
//             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//             LocalDate startDate,

//             @RequestParam(required = false)
//             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//             LocalDate endDate

//     ) {

//         DeliveryFilterRequest request = new DeliveryFilterRequest();

//         request.setDeliveryNumber(deliveryNumber);
//         request.setStationId(stationId);
//         request.setProductId(productId);
//         request.setStationInventoryId(stationInventoryId);
//         request.setStatus(status);
//         request.setStartDate(startDate);
//         request.setEndDate(endDate);

//         return deliveryService.filterDeliveries(request);
//     }


   @GetMapping
   public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>filterDeliveries(
                @ModelAttribute DeliveryFilterRequest request) {

        List<DeliveryResponse> response = deliveryService.filterDeliveries(request);
        int deliveryCount = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Deliveries retrieved successfully.",
                        deliveryCount,
                        response
                ));
    }

//     @GetMapping
//     public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
//     getAllDeliveries() {

//         List<DeliveryResponse> response =
//                 deliveryService.getAllDeliveries();
//         int deliveryCount = response.size();

//         return ResponseEntity.ok(
//                 new ApiSuccessResponse<>(
//                         LocalDateTime.now(),
//                         HttpStatus.OK.value(),
//                         "Deliveries retrieved successfully.",
//                         deliveryCount,
//                         response
//                 )
//         );
//     }


    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getStationDeliveries(
            @PathVariable Long stationId) {

        List<DeliveryResponse> response =
                deliveryService.getStationDeliveries(stationId);
        int deliveryCount = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station deliveries retrieved successfully.",
                        deliveryCount,
                        response
                )
        );
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiSuccessResponse<List<DeliveryResponse>>>
    getProductDeliveries(
            @PathVariable Long productId) {

        List<DeliveryResponse> response = deliveryService.getProductDeliveries(productId);
        int deliveryCount = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product deliveries retrieved successfully.",
                        deliveryCount,
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
            @PathVariable String status) {

        DeliveryStatus deliveryStatus =
            DeliveryStatus.valueOf(status.toUpperCase());

        List<DeliveryResponse> response =
                deliveryService.getDeliveriesByStatus(deliveryStatus);

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
        
        int recordCount = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Deliveries retrieved successfully.",
                        recordCount,
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

        int recordCount = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Station deliveries retrieved successfully.",
                        recordCount,
                        response
                )
        );
    }

}
