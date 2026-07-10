package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StockCountMapper;
import org.inventory_tracker.dto.request.CreateStockAdjustmentRequest;
import org.inventory_tracker.dto.request.CreateStockCountRequest;
import org.inventory_tracker.dto.response.StockCountResponse;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.StockCount;
import org.inventory_tracker.enums.StockAdjustmentReason;
import org.inventory_tracker.enums.StockAdjustmentType;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StockCountRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockCountService {

    private final StockCountRepository stockCountRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final ProductRepository productRepository;
    private final StockCountMapper stockCountMapper;
    private final StationRepository stationRepository;
    private final StockAdjustmentService stockAdjustmentService;

    @Transactional
    public StockCountResponse createStockCount(
            CreateStockCountRequest request) {

        validateQuantity(request.getCountedQuantity());

        StationInventory inventory =
                stationInventoryRepository.findById(
                                request.getStationInventoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station inventory not found."));

        BigDecimal systemQuantity =
                inventory.getCurrentQuantity();

        BigDecimal variance =
                request.getCountedQuantity()
                        .subtract(systemQuantity);

        StockCount stockCount =
                stockCountMapper.toEntity(request);

        stockCount.setCountNumber(
                generateCountNumber());

        stockCount.setBusinessDate(
                LocalDate.now());

        stockCount.setStationInventory(
                inventory);

        stockCount.setStation(
                inventory.getStation());

        stockCount.setProduct(
                inventory.getProduct());

        stockCount.setSystemQuantity(
                systemQuantity);

        stockCount.setVariance(
                variance);

        StockCount savedCount =
                stockCountRepository.save(stockCount);

        /*
         * Automatically create an inventory adjustment
         * whenever a variance exists.
         */
        if (variance.compareTo(BigDecimal.ZERO) != 0) {

            CreateStockAdjustmentRequest adjustmentRequest =
                    new CreateStockAdjustmentRequest();

            adjustmentRequest.setStationInventoryId(
                    inventory.getId());

            adjustmentRequest.setAdjustmentType(

                    variance.compareTo(BigDecimal.ZERO) > 0

                            ? StockAdjustmentType.INCREASE

                            : StockAdjustmentType.DECREASE

            );

            adjustmentRequest.setReason(
                    StockAdjustmentReason.STOCK_COUNT_VARIANCE);

            adjustmentRequest.setQuantity(
                    variance.abs());

            adjustmentRequest.setAdjustedBy(
                    request.getCountedBy());

            adjustmentRequest.setRemarks(
                    "Automatic adjustment from Stock Count "
                            + savedCount.getCountNumber());

            // stockAdjustmentService.createAdjustment(
            //         adjustmentRequest);
            stockAdjustmentService.createAdjustment(adjustmentRequest, savedCount);
        }

        return stockCountMapper.toResponse(savedCount);
    }

        @Transactional(readOnly = true)
    public StockCountResponse getStockCountById(Long stockCountId) {

        return stockCountMapper.toResponse(
                findStockCount(stockCountId)
        );
    }


    @Transactional(readOnly = true)
    public List<StockCountResponse> getAllStockCounts() {

        return stockCountMapper.toResponseList(

                stockCountRepository
                        .findAllByOrderByBusinessDateDescCreatedAtDesc()

        );
    }


    @Transactional(readOnly = true)
    public List<StockCountResponse> getStockCountsByStation(
            Long stationId) {

        if (!stationRepository.existsById(stationId)) {

            throw new ResourceNotFoundException(
                    "Station not found.");
        }

        return stockCountMapper.toResponseList(

                stockCountRepository
                        .findByStationIdOrderByBusinessDateDescCreatedAtDesc(
                                stationId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockCountResponse> getStockCountsByProduct(
            Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product not found.");
        }

        return stockCountMapper.toResponseList(

                stockCountRepository
                        .findByProductIdOrderByBusinessDateDescCreatedAtDesc(
                                productId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockCountResponse> getStockCountsByStationInventory(
            Long stationInventoryId) {

        if (!stationInventoryRepository.existsById(
                stationInventoryId)) {

            throw new ResourceNotFoundException(
                    "Station inventory not found.");
        }

        return stockCountMapper.toResponseList(

                stockCountRepository
                        .findByStationInventoryIdOrderByBusinessDateDescCreatedAtDesc(
                                stationInventoryId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockCountResponse> getStockCountsBetweenDates(
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

        return stockCountMapper.toResponseList(

                stockCountRepository
                        .findByBusinessDateBetweenOrderByBusinessDateDescCreatedAtDesc(
                                startDate,
                                endDate
                        )

        );
    }


    private StockCount findStockCount(Long stockCountId) {

        return stockCountRepository.findById(stockCountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock count not found."));
    }


    private void validateQuantity(BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Counted quantity must be greater than zero.");
        }
    }


    private String generateCountNumber() {

        return "CNT-"
                + LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

}
