package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateSaleRequest;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public SaleResponse createSale(
            @Valid
            @RequestBody
            CreateSaleRequest request) {

        return saleService.createSale(request);
    }

    @PostMapping("/{saleId}/complete-cash")
    public SaleResponse completeCashSale(
            @PathVariable Long saleId) {

        return saleService.completeCashSale(saleId);
    }

    @PutMapping("/{saleId}/cancel")
    public SaleResponse cancelSale(
            @PathVariable Long saleId) {

        return saleService.cancelSale(saleId);
    }

    @GetMapping("/{id}")
    public SaleResponse getSaleById(
            @PathVariable Long id) {

        return saleService.getSaleById(id);
    }

    @GetMapping("/number/{saleNumber}")
    public SaleResponse getSaleBySaleNumber(
            @PathVariable String saleNumber) {

        return saleService.getSaleBySaleNumber(saleNumber);
    }

    @GetMapping("/transaction/{transactionReference}")
    public SaleResponse getSaleByTransactionReference(
            @PathVariable String transactionReference) {

        return saleService.getSaleByTransactionReference(
                transactionReference);
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {

        return saleService.getAllSales();
    }

    @GetMapping("/station/{stationId}")
    public List<SaleResponse> getSalesByStation(
            @PathVariable Long stationId) {

        return saleService.getSalesByStation(stationId);
    }

    @GetMapping("/pump/{pumpId}")
    public List<SaleResponse> getSalesByPump(
            @PathVariable Long pumpId) {

        return saleService.getSalesByPump(pumpId);
    }

    @GetMapping("/attendant/{attendantId}")
    public List<SaleResponse> getSalesByAttendant(
            @PathVariable Long attendantId) {

        return saleService.getSalesByAttendant(attendantId);
    }

    @GetMapping("/product/{productId}")
    public List<SaleResponse> getSalesByProduct(
            @PathVariable Long productId) {

        return saleService.getSalesByProduct(productId);
    }

    @GetMapping("/payment-method/{paymentMethod}")
    public List<SaleResponse> getSalesByPaymentMethod(
            @PathVariable PaymentMethod paymentMethod) {

        return saleService.getSalesByPaymentMethod(
                paymentMethod);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public List<SaleResponse> getSalesByPaymentStatus(
            @PathVariable PaymentStatus paymentStatus) {

        return saleService.getSalesByPaymentStatus(
                paymentStatus);
    }

    @GetMapping("/status/{saleStatus}")
    public List<SaleResponse> getSalesByStatus(
            @PathVariable SaleStatus saleStatus) {

        return saleService.getSalesByStatus(
                saleStatus);
    }

    @GetMapping("/between-dates")
    public List<SaleResponse> getSalesBetweenDates(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return saleService.getSalesBetweenDates(
                startDate,
                endDate);
    }
}
