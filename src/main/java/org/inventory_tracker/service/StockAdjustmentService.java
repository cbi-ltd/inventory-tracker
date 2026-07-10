package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StockAdjustmentMapper;
import org.inventory_tracker.dto.request.CreateStockAdjustmentRequest;
import org.inventory_tracker.dto.response.StockAdjustmentResponse;
import org.inventory_tracker.entity.InventoryTransaction;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.StockAdjustment;
import org.inventory_tracker.entity.StockCount;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.enums.StockAdjustmentType;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.StockAdjustmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockAdjustmentService {

    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final StockAdjustmentMapper stockAdjustmentMapper;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;


    @Transactional
    public StockAdjustmentResponse createAdjustment(
            CreateStockAdjustmentRequest request) {

        validateQuantity(request.getQuantity());

        StationInventory inventory =
                stationInventoryRepository.findById(
                        request.getStationInventoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station inventory not found."));

        if (request.getAdjustmentType() ==
                StockAdjustmentType.DECREASE
                && inventory.getCurrentQuantity()
                .compareTo(request.getQuantity()) < 0) {

            throw new BadRequestException(
                    "Insufficient inventory available.");
        }

        StockAdjustment adjustment =
                stockAdjustmentMapper.toEntity(request);

        adjustment.setAdjustmentNumber(
                generateAdjustmentNumber());

        adjustment.setBusinessDate(LocalDate.now());

        adjustment.setStationInventory(inventory);
        adjustment.setStation(inventory.getStation());
        adjustment.setProduct(inventory.getProduct());
        adjustment.setUnitPrice(inventory.getUnitPrice());
        adjustment.setTotalAdjustmentValue(inventory.getUnitPrice().multiply(request.getQuantity()));

        InventoryTransaction transaction =
                inventoryTransactionService.recordTransaction(

                        inventory.getId(),

                        request.getAdjustmentType() == StockAdjustmentType.INCREASE
                            ? InventoryTransactionType.ADJUSTMENT_IN
                            : InventoryTransactionType.ADJUSTMENT_OUT,

                        request.getQuantity(),

                        request.getRemarks(),

                        adjustment.getAdjustmentNumber()
                );

        adjustment.setBalanceBeforeAdjustment(
                transaction.getBalanceBeforeTransaction());

        adjustment.setBalanceAfterAdjustment(
                transaction.getBalanceAfterTransaction());

        StockAdjustment saved =
                stockAdjustmentRepository.save(adjustment);

        return stockAdjustmentMapper.toResponse(saved);
    }

        @Transactional
        public StockAdjustmentResponse createAdjustment(
                CreateStockAdjustmentRequest request, StockCount stockCount) {

                validateQuantity(request.getQuantity());

                StationInventory inventory =
                        stationInventoryRepository.findById(
                                request.getStationInventoryId())
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Station inventory not found."));

                if (request.getAdjustmentType() ==
                        StockAdjustmentType.DECREASE
                        && inventory.getCurrentQuantity()
                        .compareTo(request.getQuantity()) < 0) {

                throw new BadRequestException(
                        "Insufficient inventory available.");
                }

                StockAdjustment adjustment =
                        stockAdjustmentMapper.toEntity(request);

                adjustment.setAdjustmentNumber(
                        generateAdjustmentNumber());

                adjustment.setBusinessDate(LocalDate.now());

                adjustment.setStationInventory(inventory);
                adjustment.setStation(inventory.getStation());
                adjustment.setProduct(inventory.getProduct());
                adjustment.setUnitPrice(inventory.getUnitPrice());
                adjustment.setTotalAdjustmentValue(inventory.getUnitPrice().multiply(request.getQuantity()));

                InventoryTransaction transaction =
                        inventoryTransactionService.recordTransaction(

                                inventory.getId(),

                                request.getAdjustmentType() == StockAdjustmentType.INCREASE
                                ? InventoryTransactionType.ADJUSTMENT_IN
                                : InventoryTransactionType.ADJUSTMENT_OUT,

                                request.getQuantity(),

                                request.getRemarks(),

                                adjustment.getAdjustmentNumber()
                        );

                adjustment.setBalanceBeforeAdjustment(
                        transaction.getBalanceBeforeTransaction());

                adjustment.setBalanceAfterAdjustment(
                        transaction.getBalanceAfterTransaction());
                adjustment.setStockCount(stockCount);

                StockAdjustment saved =
                        stockAdjustmentRepository.save(adjustment);

                return stockAdjustmentMapper.toResponse(saved);
        }


    @Transactional(readOnly = true)
    public StockAdjustmentResponse getAdjustmentById(Long id) {

        return stockAdjustmentMapper.toResponse(
                findAdjustment(id));
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAllAdjustments() {

        return stockAdjustmentMapper.toResponseList(
                stockAdjustmentRepository
                        .findAllByOrderByBusinessDateDescCreatedAtDesc());
    }

    private StockAdjustment findAdjustment(Long id) {

        return stockAdjustmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock adjustment not found."));
    }

    private void validateQuantity(BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Quantity must be greater than zero.");
        }
    }

    private String generateAdjustmentNumber() {

        return "ADJ-"
                + LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAdjustmentsByStation(
            Long stationId) {

        if (!stationRepository.existsById(stationId)) {

            throw new ResourceNotFoundException(
                    "Station not found.");
        }

        return stockAdjustmentMapper.toResponseList(

                stockAdjustmentRepository
                        .findByStationIdOrderByBusinessDateDescCreatedAtDesc(
                                stationId
                        )

        );
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAdjustmentsByProduct(
            Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product not found.");
        }

        return stockAdjustmentMapper.toResponseList(

                stockAdjustmentRepository
                        .findByProductIdOrderByBusinessDateDescCreatedAtDesc(
                                productId
                        )

        );
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAdjustmentsByStationInventory(
            Long stationInventoryId) {

        if (!stationInventoryRepository.existsById(stationInventoryId)) {

            throw new ResourceNotFoundException(
                    "Station inventory not found.");
        }

        return stockAdjustmentMapper.toResponseList(

                stockAdjustmentRepository
                        .findByStationInventoryIdOrderByBusinessDateDescCreatedAtDesc(
                                stationInventoryId
                        )

        );
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAdjustmentsByReason(
            StockAdjustmentReason reason) {

        return stockAdjustmentMapper.toResponseList(

                stockAdjustmentRepository
                        .findByReasonOrderByBusinessDateDescCreatedAtDesc(
                                reason
                        )

        );
    }

    @Transactional(readOnly = true)
    public List<StockAdjustmentResponse> getAdjustmentsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {

            throw new BadRequestException(
                    "Start date and end date are required.");
        }

        if (startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "Start date cannot be after end date.");
        }

        return stockAdjustmentMapper.toResponseList(

                stockAdjustmentRepository
                        .findByBusinessDateBetweenOrderByBusinessDateDescCreatedAtDesc(
                                startDate,
                                endDate
                        )

        );
    }
}
