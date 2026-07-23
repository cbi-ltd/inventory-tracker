package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.SaleMapper;
import org.inventory_tracker.dto.request.CreateSaleRequest;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.entity.*;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.enums.PaymentMethod;
import org.inventory_tracker.enums.PaymentStatus;
import org.inventory_tracker.enums.SaleStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.integration.cams.PendingPayment.PendingTransferService;
import org.inventory_tracker.repository.*;
import org.inventory_tracker.util.ShiftUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final StationRepository stationRepository;
    private final PumpRepository pumpRepository;
    private final TerminalRepository terminalRepository;
    private final AttendantRepository attendantRepository;
    private final ProductRepository productRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final PendingTransferService pendingTransferService;

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {

        Station station = stationRepository.findById(request.getStationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        Pump pump = pumpRepository.findById(request.getPumpId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pump not found"));

        Attendant attendant = attendantRepository.findById(request.getAttendantId())
                                .orElseThrow(() -> new ResourceNotFoundException("Attendant not found"));

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() ->
                                new ResourceNotFoundException("Product not found"));

        Terminal terminal = null;

        if (request.getTerminalId() != null) {
            terminal = terminalRepository.findById(request.getTerminalId())
                            .orElseThrow(() -> new ResourceNotFoundException("Terminal not found"));
        }

        StationInventory inventory = stationInventoryRepository.findByStationIdAndProductId(station.getId(), product.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));

        if (inventory.getCurrentQuantity().compareTo(request.getQuantity()) < 0) {
            throw new BadRequestException("Insufficient stock available.");
        }

        Sale sale = saleMapper.toEntity(request);
        sale.setStation(station);
        sale.setPump(pump);
        sale.setTerminal(terminal);
        sale.setAttendant(attendant);
        sale.setProduct(product);
        sale.setSaleNumber(generateSaleNumber(station));
        sale.setReceiptNumber(generateReceiptNumber());
        sale.setSaleTime(LocalDateTime.now());

        BigDecimal unitPrice = request.getUnitPrice() != null ? request.getUnitPrice() : inventory.getSellingPrice();
        sale.setUnitPrice(unitPrice);

        BigDecimal gross = calculateGrossAmount(request.getQuantity(), unitPrice);
                // unitPrice.multiply(request.getQuantity());
        sale.setGrossAmount(gross);

        BigDecimal discount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();
        sale.setDiscountAmount(discount);

        // sale.setNetAmount(gross.subtract(discount));
        sale.setNetAmount(calculateNetAmount(gross, discount));

        sale.setInventoryUpdated(false);

        switch (request.getPaymentMethod()) {
            case CASH -> {
                sale.setPaymentStatus(PaymentStatus.SUCCESS);
                sale.setSaleStatus(SaleStatus.PENDING);
            }

            case CARD, TRANSFER, MIXED -> {
                sale.setPaymentStatus(PaymentStatus.PENDING);
                sale.setSaleStatus(SaleStatus.PENDING);
            }

            default -> throw new BadRequestException(
                    "Unsupported payment method.");
        }

        sale = saleRepository.save(sale);
        pendingTransferService.registerPendingTransfer(station.getVirtualAccountNumber(), sale.getSaleNumber(), sale.getNetAmount(), terminal.getTerminalSerialNumber());

        // if(pendingTransferRepository.findByVirtualAccountNumberAndSaleNumber(pendingTransfer.getVirtualAccountNumber(), pendingTransfer.getSaleNumber()) != null){
        //         notifyFuelFlow(requestRef, externalReference, virtualAccount.get(), amount, jsonNotification);
        // }

        if (sale.getPaymentMethod() == PaymentMethod.CASH) {
            return completeCashSale(sale.getId());
        }

        return saleMapper.toResponse(sale);
    }


    @Transactional
    public SaleResponse completeCashSale(Long saleId) {

        Sale sale =
                saleRepository.findById(saleId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sale not found"));

        if (sale.getInventoryUpdated()) { return saleMapper.toResponse(sale); }

        StationInventory inventory =
                stationInventoryRepository
                        .findByStationIdAndProductId(
                                sale.getStation().getId(),
                                sale.getProduct().getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station inventory not found"));

        Long inventoryId = getStationInventoryId(sale.getStation().getId(), sale.getProduct().getId());

        if (inventory.getCurrentQuantity().compareTo(sale.getQuantity()) < 0) {
            throw new BadRequestException("Insufficient inventory.");
        }

        inventoryTransactionService.recordTransaction(inventoryId, InventoryTransactionType.SALE, sale.getQuantity(), sale.getSaleNumber(), "SALE" + sale.getSaleNumber());

        sale.setInventoryUpdated(true);
        sale.setPaymentStatus(PaymentStatus.SUCCESS);
        sale.setSaleStatus(SaleStatus.COMPLETED);
        sale.setTransactionReference(sale.getSaleNumber());

        saleRepository.save(sale);
        return saleMapper.toResponse(sale);
    }

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

    // public SaleResponse completeElectronicSale(String transactionReference, Payment payment){

    // }


        private String generateSaleNumber(Station station) {
            return "FuelFlow-"
                    + ShiftUtil.businessDate(station.getTimeZone())
                    + "-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        }

        private String generateReceiptNumber() {

                    return "RCP-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();
        }

        private BigDecimal calculateGrossAmount(BigDecimal quantity, BigDecimal unitPrice) {
            return quantity.multiply(unitPrice);
        }

        private BigDecimal calculateNetAmount(BigDecimal grossAmount, BigDecimal discount) {
            if (discount == null) { discount = BigDecimal.ZERO; }
            return grossAmount.subtract(discount);
        }

        private Long getStationInventoryId(Long stationId, Long productId) {

            return stationInventoryRepository.findByStationIdAndProductId(stationId, productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found."))
                    .getId();
        }
}
