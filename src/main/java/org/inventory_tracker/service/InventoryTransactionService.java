package org.inventory_tracker.service;


import org.inventory_tracker.entity.InventoryTransaction;
import org.inventory_tracker.entity.Product;
// import org.inventory_tracker.entity.Merchant;
import org.inventory_tracker.repository.InventoryTransactionRepository;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.entity.StationInventory;
// import org.inventory_tracker.entity.security.MerchantContext;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.security.AuthenticatedUserService;
import org.inventory_tracker.security.MerchantPrincipal;
import org.inventory_tracker.exception.BadRequestException;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.util.ShiftUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;



@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTransactionService {

    private final StationInventoryRepository stationInventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public InventoryTransaction recordTransactionForDashboard(
            Long stationInventoryId,
            InventoryTransactionType transactionType,
            BigDecimal quantity,
            String remarks,
            String referenceNumber
    ) {

        validateQuantity(quantity);

        MerchantPrincipal principal = authenticatedUserService.getCurrentUser();
        if (principal == null) {
            throw new ResourceNotFoundException("Merchant is not authenticated");
        }

        // StationInventory stationInventory = stationInventoryRepository.findByIdAndStation_Merchant_Id(stationInventoryId, merchant.getId())
        //         .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));
        StationInventory stationInventory = stationInventoryRepository.findById(stationInventoryId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));
        Station station = stationInventory.getStation();
        if (!station.getMerchant().getId().equals(principal.getMerchantDbId())) {
            throw new ResourceNotFoundException("Station inventory not found");
        }

        BigDecimal balanceBefore = stationInventory.getCurrentQuantity();
        validateTransaction(balanceBefore, quantity, transactionType);

        BigDecimal balanceAfter = calculateNewBalance(balanceBefore, quantity, transactionType);
        stationInventory.setCurrentQuantity(balanceAfter);

        stationInventoryRepository.save(stationInventory);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setStationInventory(stationInventory);
        transaction.setStation(stationInventory.getStation());
        transaction.setProduct(stationInventory.getProduct());
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(quantity);
        transaction.setBalanceBeforeTransaction(balanceBefore);
        transaction.setBalanceAfterTransaction(balanceAfter);
        transaction.setRemarks(remarks);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setBusinessDate(ShiftUtil.businessDate(station.getTimeZone()));
        transaction.setTransactionTime(LocalDateTime.now(station.getTimeZone()));

        return inventoryTransactionRepository.save(transaction);
    }

    private void validateTransaction(
            BigDecimal currentBalance,
            BigDecimal quantity,
            InventoryTransactionType transactionType
    ) {

        if (isOutboundTransaction(transactionType)
                && currentBalance.compareTo(quantity) < 0) {

            throw new BadRequestException(
                    "Insufficient inventory. Current balance is "
                            + currentBalance
                            + " but requested quantity is "
                            + quantity
            );
        }
    }


    @Transactional
    public InventoryTransaction recordTransaction(
            Long stationInventoryId,
            BigDecimal quantity,
            String remarks,
            String referenceNumber,
            Station expectedStation,
            Product expectedProduct
    ) {

        validateQuantity(quantity);

        StationInventory stationInventory =
                stationInventoryRepository.findById(stationInventoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Station inventory not found"));

        Station station = stationInventory.getStation();

        if (station == null) {
            throw new ResourceNotFoundException(
                    "Station inventory is not associated with a station");
        }

        if (station.getMerchant() == null) {
            throw new ResourceNotFoundException(
                    "Station is not associated with a merchant");
        }

        // Make sure the inventory belongs to the sale's station.
        if (!station.getId().equals(expectedStation.getId())) {
            throw new ResourceNotFoundException(
                    "Station inventory not found");
        }

        // Make sure the inventory is for the sale's product.
        if (!stationInventory.getProduct().getId()
                .equals(expectedProduct.getId())) {

            throw new ResourceNotFoundException(
                    "Station inventory not found");
        }

        BigDecimal balanceBefore =
                stationInventory.getCurrentQuantity();

        validateTransaction(
                balanceBefore,
                quantity,
                InventoryTransactionType.SALE);

        BigDecimal balanceAfter =
                calculateNewBalance(
                        balanceBefore,
                        quantity,
                        InventoryTransactionType.SALE);

        stationInventory.setCurrentQuantity(balanceAfter);

        stationInventoryRepository.save(stationInventory);

        InventoryTransaction transaction =
                new InventoryTransaction();

        transaction.setStationInventory(stationInventory);
        transaction.setStation(stationInventory.getStation());
        transaction.setProduct(stationInventory.getProduct());
        transaction.setTransactionType(
                InventoryTransactionType.SALE);
        transaction.setQuantity(quantity);
        transaction.setBalanceBeforeTransaction(
                balanceBefore);
        transaction.setBalanceAfterTransaction(
                balanceAfter);
        transaction.setRemarks(remarks);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setBusinessDate(
                ShiftUtil.businessDate(station.getTimeZone()));
        transaction.setTransactionTime(
                LocalDateTime.now(station.getTimeZone()));

        return inventoryTransactionRepository.save(transaction);
    }


    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal quantity, InventoryTransactionType transactionType) {
        if (isInboundTransaction(transactionType)) {
            return currentBalance.add(quantity);
        }

        if (isOutboundTransaction(transactionType)) {
            return currentBalance.subtract(quantity);
        }

        throw new BadRequestException("Unsupported inventory transaction type: "+ transactionType);
    }


    private boolean isInboundTransaction(InventoryTransactionType transactionType) {
        return switch (transactionType) {

            case OPENING_STOCK,
                    DELIVERY,
                    TRANSFER_IN,
                    RETURN,
                    ADJUSTMENT_IN -> true;

            default -> false;
        };
    }


    private boolean isOutboundTransaction(InventoryTransactionType transactionType) {
        return switch (transactionType) {

            case SALE,
                    TRANSFER_OUT,
                    DELIVERY_REVERSAL,
                    LOSS,
                    ADJUSTMENT_OUT -> true;

            default -> false;
        };
    }


    /**
     * Basic quantity validation.
     */
    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null) {
            throw new BadRequestException("Quantity is required.");
        }

        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        /*
         * Only ADJUSTMENT and STOCK_COUNT
         * are allowed to eventually work with
         * signed quantities (after higher-level
         * services determine the variance).
         *
         * For all other transaction types,
         * the quantity passed into recordTransaction()
         * should always be positive.
         */
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Quantity cannot be negative.");
        }
    }
}
