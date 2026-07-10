package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.config.mapper.StockTransferMapper;
import org.inventory_tracker.dto.request.CreateStockTransferRequest;
import org.inventory_tracker.dto.response.StockTransferResponse;
import org.inventory_tracker.entity.InventoryTransaction;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.entity.StockTransfer;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.enums.StockTransferStatus;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StockTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.ProductRepository;


@Service
@RequiredArgsConstructor
public class StockTransferService {

    private final StockTransferRepository stockTransferRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final StockTransferMapper stockTransferMapper;
    private final StationRepository stationRepository;
    private final ProductRepository productRepository;


    @Transactional
    public StockTransferResponse createTransfer(
            CreateStockTransferRequest request) {

        StationInventory sourceInventory =
                stationInventoryRepository.findById(
                                request.getSourceInventoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Source inventory not found."));

        StationInventory destinationInventory =
                stationInventoryRepository.findById(
                                request.getDestinationInventoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Destination inventory not found."));

        /*
         * Source and destination cannot be the same inventory.
         */
        if (sourceInventory.getId().equals(destinationInventory.getId())) {

            throw new BadRequestException(
                    "Source and destination inventory cannot be the same.");
        }

        /*
         * Only inventories of the same product can be transferred.
         */
        Product sourceProduct = sourceInventory.getProduct();
        Product destinationProduct = destinationInventory.getProduct();

        if (!sourceProduct.getId().equals(destinationProduct.getId())) {

            throw new BadRequestException(
                    "Stock transfers can only occur between inventories of the same product.");
        }

        /*
         * Quantity validation.
         */
        if (request.getQuantityTransferred() == null
                || request.getQuantityTransferred().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Transfer quantity must be greater than zero.");
        }

        /*
         * Ensure source has sufficient stock.
         */
        if (sourceInventory.getCurrentQuantity()
                .compareTo(request.getQuantityTransferred()) < 0) {

            throw new BadRequestException(
                    "Insufficient stock available for transfer.");
        }

        StockTransfer transfer =
                stockTransferMapper.toEntity(request);

        transfer.setTransferNumber(generateTransferNumber());

        transfer.setSourceInventory(sourceInventory);

        transfer.setDestinationInventory(destinationInventory);

        transfer.setSourceStation(sourceInventory.getStation());

        transfer.setDestinationStation(destinationInventory.getStation());

        transfer.setProduct(sourceProduct);

        transfer.setBusinessDate(LocalDate.now());

        transfer.setStatus(StockTransferStatus.PENDING);

        transfer.setInitiatedBy("SYSTEM");

        /*
         * Snapshot pricing.
         */
        transfer.setUnitPrice(sourceInventory.getUnitPrice());

        transfer.setTotalValueTransferred(
                transfer.getUnitPrice()
                        .multiply(request.getQuantityTransferred()));

        /*
         * Balance snapshots are populated
         * when the transfer is completed.
         */
        transfer.setSourceBalanceBeforeTransfer(null);
        transfer.setSourceBalanceAfterTransfer(null);
        transfer.setDestinationBalanceBeforeTransfer(null);
        transfer.setDestinationBalanceAfterTransfer(null);

        StockTransfer saved =
                stockTransferRepository.save(transfer);

        return stockTransferMapper.toResponse(saved);
    }


    @Transactional
    public StockTransferResponse completeTransfer(Long transferId) {

        StockTransfer transfer =
                stockTransferRepository.findById(transferId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stock transfer not found."));

        /*
        * Only pending transfers can be completed.
        */
        if (transfer.getStatus() == StockTransferStatus.COMPLETED) {

            throw new BadRequestException(
                    "Stock transfer has already been completed.");
        }

        if (transfer.getStatus() == StockTransferStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled transfers cannot be completed.");
        }

        StationInventory sourceInventory =
                transfer.getSourceInventory();

        StationInventory destinationInventory =
                transfer.getDestinationInventory();

        /*
        * Safety check.
        */
        if (sourceInventory.getCurrentQuantity()
                .compareTo(transfer.getQuantityTransferred()) < 0) {

            throw new BadRequestException(
                    "Insufficient stock available to complete transfer.");
        }

        /*
        * Record TRANSFER OUT.
        */
        InventoryTransaction transferOut =
                inventoryTransactionService.recordTransaction(

                        sourceInventory.getId(),

                        InventoryTransactionType.TRANSFER_OUT,

                        transfer.getQuantityTransferred(),

                        transfer.getRemarks(),

                        transfer.getTransferNumber()
                );

        /*
        * Record TRANSFER IN.
        */
        InventoryTransaction transferIn =
                inventoryTransactionService.recordTransaction(

                        destinationInventory.getId(),

                        InventoryTransactionType.TRANSFER_IN,

                        transfer.getQuantityTransferred(),

                        transfer.getRemarks(),

                        transfer.getTransferNumber()
                );

        transfer.setTransferOutTransaction(transferOut);
        transfer.setTransferInTransaction(transferIn);

        /*
        * Snapshot balances directly from the ledger.
        */
        transfer.setSourceBalanceBeforeTransfer(
                transferOut.getBalanceBeforeTransaction());

        transfer.setSourceBalanceAfterTransfer(
                transferOut.getBalanceAfterTransaction());

        transfer.setDestinationBalanceBeforeTransfer(
                transferIn.getBalanceBeforeTransaction());

        transfer.setDestinationBalanceAfterTransfer(
                transferIn.getBalanceAfterTransaction());

        /*
        * Completion metadata.
        */
        transfer.setCompletedAt(LocalDateTime.now());

        transfer.setCompletedBy("SYSTEM");

        transfer.setStatus(StockTransferStatus.COMPLETED);

            /*
        * Persist completed transfer.
        */
        StockTransfer completedTransfer =
                stockTransferRepository.save(transfer);

        return stockTransferMapper.toResponse(
                completedTransfer
        );

    }


    private String generateTransferNumber() {

        return "TRF-"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();
    }


    @Transactional
    public StockTransferResponse cancelTransfer(Long transferId) {

        StockTransfer transfer = stockTransferRepository.findById(transferId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock transfer not found."));

        if (transfer.getStatus() == StockTransferStatus.COMPLETED) {

            throw new BadRequestException(
                    "Completed transfers cannot be cancelled.");
        }

        if (transfer.getStatus() == StockTransferStatus.CANCELLED) {

            throw new BadRequestException(
                    "Stock transfer has already been cancelled.");
        }

        transfer.setStatus(StockTransferStatus.CANCELLED);

        StockTransfer cancelledTransfer =
                stockTransferRepository.save(transfer);

        return stockTransferMapper.toResponse(cancelledTransfer);
    }


    @Transactional(readOnly = true)
    public StockTransferResponse getTransferById(Long transferId) {

        return stockTransferMapper.toResponse(
                findTransfer(transferId)
        );
    }


    @Transactional(readOnly = true)
    public StockTransferResponse getTransferByTransferNumber(
            String transferNumber) {

        StockTransfer transfer =
                stockTransferRepository
                        .findByTransferNumber(transferNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Stock transfer not found."));

        return stockTransferMapper.toResponse(transfer);
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getAllTransfers() {

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findAllByOrderByBusinessDateDescCompletedAtDesc()

        );
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByStatus(
            StockTransferStatus status) {

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findByStatusOrderByBusinessDateDescCompletedAtDesc(
                                status
                        )

        );
    }

    public List<StockTransfer> getPendingTransfers(Long stationId){
        return stockTransferRepository.findPendingTransfersBySourceStation(stationId);
    }

    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByProduct(Long productId) {

        if (!productRepository.existsById(productId)) {

            throw new ResourceNotFoundException(
                    "Product not found.");
        }

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findByProductIdOrderByBusinessDateDescCompletedAtDesc(
                                productId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersBySourceStation(
            Long stationId) {

        if (!stationRepository.existsById(stationId)) {

            throw new ResourceNotFoundException(
                    "Station not found.");
        }

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findBySourceStationIdOrderByBusinessDateDescCompletedAtDesc(
                                stationId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByDestinationStation(
            Long stationId) {

        if (!stationRepository.existsById(stationId)) {

            throw new ResourceNotFoundException(
                    "Station not found.");
        }

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findByDestinationStationIdOrderByBusinessDateDescCompletedAtDesc(
                                stationId
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersBetweenDates(
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

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findByBusinessDateBetweenOrderByBusinessDateDescCompletedAtDesc(
                                startDate,
                                endDate
                        )

        );
    }


    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersBySourceInventory(
            Long stationInventoryId) {

        if (!stationInventoryRepository.existsById(stationInventoryId)) {

            throw new ResourceNotFoundException(
                    "Station inventory not found.");
        }

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findBySourceInventoryIdOrderByBusinessDateDescCompletedAtDesc(
                                stationInventoryId
                        )

        );
    }

    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByDestinationInventory(
            Long stationInventoryId) {

        if (!stationInventoryRepository.existsById(stationInventoryId)) {

            throw new ResourceNotFoundException(
                    "Station inventory not found.");
        }

        return stockTransferMapper.toResponseList(

                stockTransferRepository
                        .findByDestinationInventoryIdOrderByBusinessDateDescCompletedAtDesc(
                                stationInventoryId
                        )

        );
    }

    // HELPER FUNCTIONS
    // ------------------------------------------------------------

    private StockTransfer findTransfer(Long transferId) {

        return stockTransferRepository.findById(transferId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock transfer not found."));
    }

    private void validatePendingTransfer(StockTransfer transfer) {

        if (transfer.getStatus() == StockTransferStatus.COMPLETED) {

            throw new BadRequestException(
                    "Stock transfer has already been completed.");
        }

        if (transfer.getStatus() == StockTransferStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled transfers cannot be modified.");
        }
    }

    // StockTransfer transfer = findTransfer(id);
    // validatePendingTransfer(transfer);

     private void validateTransferQuantity(BigDecimal quantity) {

        if (quantity == null
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    "Transfer quantity must be greater than zero.");
        }
    }

    // if (request.getQuantityTransferred() == null
    //     || request.getQuantityTransferred().compareTo(BigDecimal.ZERO) <= 0)

    private void validateInventories(StationInventory sourceInventory, StationInventory destinationInventory) {

        if (sourceInventory.getId().equals(destinationInventory.getId())) {

            throw new BadRequestException(
                    "Source and destination inventory cannot be the same.");
        }

        if (!sourceInventory.getProduct().getId()
                .equals(destinationInventory.getProduct().getId())) {

            throw new BadRequestException(
                    "Stock transfers can only occur between inventories of the same product.");
        }
    }

    // removes a lot of duplication from createTransfer()

    private void validateAvailableStock(StationInventory sourceInventory, BigDecimal quantity) {

        if (sourceInventory.getCurrentQuantity()
                .compareTo(quantity) < 0) {

            throw new BadRequestException(
                    "Insufficient stock available.");
        }
    }


}
