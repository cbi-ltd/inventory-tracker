package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;

import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.request.CreateSaleRequest;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.enums.Shift;
import org.inventory_tracker.service.PaymentService;
import org.inventory_tracker.service.SaleService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Sales", description = "Sale management APIs")
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final PaymentService paymentService;

    @PostMapping
    @SecurityRequirements
    public SaleResponse createSale(@Valid @RequestBody CreateSaleRequest request) {
        return paymentService.createSale(request);
    }

    @PostMapping("/{saleId}/complete-cash")
    @SecurityRequirements
    public SaleResponse completeCashSale(@PathVariable Long saleId) {
        return paymentService.completeCashSale(saleId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{saleId}/cancel")
    public SaleResponse cancelSale(@PathVariable Long saleId) {
        return saleService.cancelSale(saleId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/number/{saleNumber}")
    public SaleResponse getSaleBySaleNumber(@PathVariable String saleNumber) {
        return saleService.getSaleBySaleNumber(saleNumber);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/transaction/{transactionReference}")
    public SaleResponse getSaleByTransactionReference(@PathVariable String transactionReference) {
        return saleService.getSaleByTransactionReference(transactionReference);
    }

    // @SecurityRequirement(name = "bearerAuth")
    // @GetMapping
    // public List<SaleResponse> getAllSales() {
    //     return saleService.getAllSales();
    // }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<SaleResponse>>> getSales(
        @RequestParam(required = false) Long saleId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) Shift shift,
        @RequestParam(required = false) List<Shift> shifts,
        @RequestParam(required = false) String terminalSerialNumber,
        @RequestParam(required = false) Long pumpId
    ) {
        List<SaleResponse> sales = saleService.getSales(saleId, startDate, endDate, shift, shifts, terminalSerialNumber, pumpId);
        int size = sales.size();
        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Sales retrieved successfully", size, sales));
    }

    @GetMapping("/pos")
    @SecurityRequirements
    public ResponseEntity<ApiSuccessResponse<Page<SaleResponse>>> getPosSales(
        @RequestParam(required = false) Long saleId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) Shift shift,
        @RequestParam(required = false) List<Shift> shifts,
        @RequestParam(required = false) String terminalSerialNumber,
        @RequestParam(required = false) Long pumpId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<SaleResponse> sales = saleService.getPosSales(saleId, startDate, endDate, shift, shifts, terminalSerialNumber, pumpId, page, size);
        // int totalItems = sales.getNumberOfElements();  TOTAL ON CURRENT PAGE
        int totalItems = (int) sales.getTotalElements();  // TOTAL ACROSS ALL PAGES

        // IF WANT TO RETURN LIST INSTEAD OF A PAGE: return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Sales retrieved successfully", totalItems, sales.getContent()));
        return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Sales retrieved successfully", totalItems, sales));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/station/{stationId}")
    public List<SaleResponse> getSalesByStation(@PathVariable Long stationId) {
        return saleService.getSalesByStation(stationId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/pump/{pumpId}")
    public List<SaleResponse> getSalesByPump(@PathVariable Long pumpId) {
        return saleService.getSalesByPump(pumpId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/attendant/{attendantId}")
    public List<SaleResponse> getSalesByAttendant(@PathVariable Long attendantId) {
        return saleService.getSalesByAttendant(attendantId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/product/{productId}")
    public List<SaleResponse> getSalesByProduct(@PathVariable Long productId) {
        return saleService.getSalesByProduct(productId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/payment-method/{paymentMethod}")
    public List<SaleResponse> getSalesByPaymentMethod(@PathVariable PaymentMethod paymentMethod) {
        return saleService.getSalesByPaymentMethod(paymentMethod);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/payment-status/{paymentStatus}")
    public List<SaleResponse> getSalesByPaymentStatus(@PathVariable PaymentStatus paymentStatus) {
        return saleService.getSalesByPaymentStatus(paymentStatus);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/status/{saleStatus}")
    public List<SaleResponse> getSalesByStatus(@PathVariable SaleStatus saleStatus) {
        return saleService.getSalesByStatus(saleStatus);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/between-dates")
    public List<SaleResponse> getSalesBetweenDates(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return saleService.getSalesBetweenDates(startDate, endDate);
    }
}
