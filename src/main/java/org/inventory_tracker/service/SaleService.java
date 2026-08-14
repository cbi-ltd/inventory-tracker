package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.SaleMapper;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.entity.*;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.integration.cams.PendingPayment.card.PendingCardPaymentService;
import org.inventory_tracker.integration.cams.PendingPayment.transfer.PendingTransferService;
import org.inventory_tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final PumpRepository pumpRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final PendingTransferService pendingTransferService;
    private final PendingCardPaymentService pendingCardPaymentService;

//     @Transactional
//     public SaleResponse createSale(CreateSaleRequest request) {

//         try {
//                 Pump pump = pumpRepository.findById(request.getPumpId())
//                                 .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));

//                 PumpAssignment assignment = pumpAssignmentRepository.findFirstByPumpIdAndActiveTrue(pump.getId())
//                                                 .orElseThrow(() ->new ResourceNotFoundException("Pump is not currently assigned."));

//                 Terminal terminal = assignment.getTerminal();
//                 Attendant attendant = assignment.getAttendant();

//                 Station station = pump.getStation();
//                 Product product = pump.getProduct();

//                 StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(station.getId(), product.getId())
//                                                 .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

//                 BigDecimal unitPrice = inventory.getUnitPrice();
//                 BigDecimal quantity;
//                 BigDecimal grossAmount;

//                 if (request.getQuantity() != null) {
//                         quantity = request.getQuantity();
//                         grossAmount =quantity.multiply(unitPrice);
//                 }
//                 else {
//                         grossAmount = request.getAmount();
//                         if (unitPrice.compareTo(BigDecimal.ZERO) == 0) { throw new BadRequestException("Unit price cannot be zero."); }
//                         quantity = grossAmount.divide(unitPrice, 3, RoundingMode.HALF_UP);
//                 }

//                 if (inventory.getCurrentQuantity().compareTo(quantity) < 0) {
//                         throw new BadRequestException("Insufficient stock available.");
//                 }

//                 Sale sale = saleMapper.toEntity(request);
//                 sale.setStation(station);
//                 sale.setPump(pump);
//                 sale.setTerminal(terminal);
//                 sale.setAttendant(attendant);
//                 sale.setProduct(product);
//                 sale.setSaleNumber(generateSaleNumber(station));
//                 sale.setReceiptNumber(generateReceiptNumber());
//                 sale.setSaleTime(LocalDateTime.now());
//                 sale.setUnitPrice(unitPrice);
//                 sale.setQuantity(quantity);

//                 // BigDecimal gross = calculateGrossAmount(request.getQuantity(), unitPrice);
//                         // unitPrice.multiply(request.getQuantity());
//                 sale.setGrossAmount(grossAmount);

//                 BigDecimal discount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();
//                 sale.setDiscountAmount(discount);

//                 // sale.setNetAmount(gross.subtract(discount));
//                 sale.setNetAmount(calculateNetAmount(grossAmount, discount));

//                 sale.setInventoryUpdated(false);

//                 switch (request.getPaymentMethod()) {
//                 case CASH -> {
//                         sale.setPaymentStatus(PaymentStatus.SUCCESS);
//                         sale.setSaleStatus(SaleStatus.PENDING);
//                 }

//                 case CARD, TRANSFER, MIXED -> {
//                         sale.setPaymentStatus(PaymentStatus.PENDING);
//                         sale.setSaleStatus(SaleStatus.PENDING);
//                 }

//                 default -> throw new BadRequestException(
//                         "Unsupported payment method.");
//                 }

//                 saleRepository.save(sale);

//                 switch (request.getPaymentMethod()) {
//                         case TRANSFER -> pendingTransferService.registerPendingTransfer(station.getVirtualAccountNumber(), sale.getSaleNumber(), sale.getNetAmount(), terminal.getTerminalSerialNumber());
//                         case CARD -> pendingCardPaymentService.register(sale.getSaleNumber(), sale.getNetAmount(),terminal.getTerminalSerialNumber(), terminal.getTid());
//                         case CASH ->  {}
//                         case MIXED -> {}
//                 }

//                 if (sale.getPaymentMethod() == PaymentMethod.CASH) {
//                         recordCashPayment(sale.getId());
//                         return completeCashSale(sale.getId());
//                 }

//                 return saleMapper.toResponse(sale);
//         }
//         catch (ResourceNotFoundException | BadRequestException e) { throw e; } 
//         catch (ArithmeticException e) {
//                 throw new BadRequestException("Calculation error during transaction: " + e.getMessage());
//         } 
//         catch (Exception e) {
//                 throw new RuntimeException("Failed to process sale due to an internal error: " + e.getMessage(), e);
//         }
//     }

//     @Transactional
//     public SaleResponse completeSale(Long saleId, String transactionReference, PaymentStatus paymentStatus, LocalDateTime paidAt) {

//         Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

//         if (sale.getInventoryUpdated()) { return saleMapper.toResponse(sale); }

//         StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(sale.getStation().getId(), sale.getProduct().getId())
//                                                 .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

//         if (inventory.getCurrentQuantity().compareTo(sale.getQuantity()) < 0) {
//                 throw new BadRequestException("Insufficient inventory.");
//         }

//         Long inventoryId = inventory.getId();

//         inventoryTransactionService.recordTransaction(
//                 inventoryId,
//                 InventoryTransactionType.SALE,
//                 sale.getQuantity(),
//                 sale.getSaleNumber(),
//                 "SALE-" + sale.getSaleNumber());

//         sale.setInventoryUpdated(true);
//         sale.setPaymentStatus(paymentStatus);
//         sale.setSaleStatus(SaleStatus.COMPLETED);
//         sale.setTransactionReference(transactionReference);
//         sale.setPaidAt(paidAt);

//         saleRepository.save(sale);
//         return saleMapper.toResponse(sale);
//     }




//     @Transactional
//     public SaleResponse completeCashSale(Long saleId) {

//         Sale sale =
//                 saleRepository.findById(saleId)
//                         .orElseThrow(() ->
//                                 new ResourceNotFoundException(
//                                         "Sale not found"));

//         if (sale.getInventoryUpdated()) { return saleMapper.toResponse(sale); }

//         StationInventory inventory =
//                 stationInventoryRepository
//                         .findByStationIdAndProductId(
//                                 sale.getStation().getId(),
//                                 sale.getProduct().getId())
//                         .orElseThrow(() ->
//                                 new ResourceNotFoundException(
//                                         "Station inventory not found"));

//         Long inventoryId = getStationInventoryId(sale.getStation().getId(), sale.getProduct().getId());

//         if (inventory.getCurrentQuantity().compareTo(sale.getQuantity()) < 0) {
//             throw new BadRequestException("Insufficient inventory.");
//         }

//         inventoryTransactionService.recordTransaction(inventoryId, InventoryTransactionType.SALE, sale.getQuantity(), sale.getSaleNumber(), "SALE" + sale.getSaleNumber());

//         sale.setInventoryUpdated(true);
//         sale.setPaymentStatus(PaymentStatus.SUCCESS);
//         sale.setSaleStatus(SaleStatus.COMPLETED);
//         sale.setTransactionReference(sale.getSaleNumber());

//         saleRepository.save(sale);
//         return saleMapper.toResponse(sale);
//     }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {

        return saleRepository.findById(id)

                .map(saleMapper::toResponse)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sale not found."));
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleBySaleNumber(String saleNumber) {

        return saleRepository.findBySaleNumber(saleNumber).map(saleMapper::toResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Sale not found."));
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleByTransactionReference(
            String transactionReference) {

        return saleRepository.findByTransactionReference(
                        transactionReference)

                .map(saleMapper::toResponse)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sale not found."));
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {

        return saleRepository

                .findAllByOrderByBusinessDateDescSaleTimeDesc()

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByStation(Long stationId) {

        return saleRepository

                .findByStationIdOrderByBusinessDateDescSaleTimeDesc(
                        stationId)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByPump(Long pumpId) {

        return saleRepository

                .findByPumpIdOrderByBusinessDateDescSaleTimeDesc(
                        pumpId)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByAttendant(Long attendantId) {

        return saleRepository

                .findByAttendantIdOrderByBusinessDateDescSaleTimeDesc(
                        attendantId)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByProduct(Long productId) {

        return saleRepository

                .findByProductIdOrderByBusinessDateDescSaleTimeDesc(
                        productId)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByPaymentMethod(
            PaymentMethod paymentMethod) {

        return saleRepository

                .findByPaymentMethodOrderByBusinessDateDescSaleTimeDesc(
                        paymentMethod)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByPaymentStatus(
            PaymentStatus paymentStatus) {

        return saleRepository

                .findByPaymentStatusOrderByBusinessDateDescSaleTimeDesc(
                        paymentStatus)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByStatus(
            SaleStatus saleStatus) {

        return saleRepository

                .findBySaleStatusOrderByBusinessDateDescSaleTimeDesc(
                        saleStatus)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date.");
        }

        return saleRepository

                .findByBusinessDateBetween(
                        startDate,
                        endDate)

                .stream()

                .map(saleMapper::toResponse)

                .toList();
    }

    @Transactional
    public SaleResponse cancelSale(Long saleId) {

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sale not found."));

        if (sale.getSaleStatus() == SaleStatus.CANCELLED) {

            throw new BadRequestException(
                    "Sale has already been cancelled.");
        }

        if (sale.getSaleStatus() == SaleStatus.COMPLETED) {

            throw new BadRequestException(
                    "Completed sales cannot be cancelled. Use a refund or reversal process instead.");
        }

        sale.setSaleStatus(SaleStatus.CANCELLED);

        sale.setPaymentStatus(PaymentStatus.CANCELLED);

        return saleMapper.toResponse(
                saleRepository.save(sale));
    }


        // private String generateSaleNumber(Station station) {
        //     return "FuelFlow-"
        //             + ShiftUtil.businessDate(station.getTimeZone())
        //             + "-"
        //             + UUID.randomUUID()
        //             .toString()
        //             .substring(0, 8)
        //             .toUpperCase();
        // }

        // private String generateReceiptNumber() {

        //             return "RCP-"
        //                     + UUID.randomUUID()
        //                     .toString()
        //                     .substring(0, 8)
        //                     .toUpperCase();
        // }

        // private BigDecimal calculateGrossAmount(BigDecimal quantity, BigDecimal unitPrice) {
        //     return quantity.multiply(unitPrice);
        // }

        // private BigDecimal calculateNetAmount(BigDecimal grossAmount, BigDecimal discount) {
        //     if (discount == null) { discount = BigDecimal.ZERO; }
        //     return grossAmount.subtract(discount);
        // }

        private Long getStationInventoryId(Long stationId, Long productId) {

            return stationInventoryRepository.findByStationIdAndProductId(stationId, productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found."))
                    .getId();
        }
}
