package org.inventory_tracker.service;


import org.inventory_tracker.entity.InventoryTransaction;
import org.inventory_tracker.repository.InventoryTransactionRepository;
import org.inventory_tracker.enums.InventoryTransactionType;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.repository.StationInventoryRepository;
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

    /**
     * Records a stock movement and updates the running inventory balance.
     *
     * This is the ONLY method in the application allowed to mutate
     * StationInventory.currentQuantity.
     */
    public InventoryTransaction recordTransaction(
            Long stationInventoryId,
            InventoryTransactionType transactionType,
            BigDecimal quantity,
            String remarks,
            String referenceNumber
    ) {

        validateQuantity(quantity);

        StationInventory stationInventory = stationInventoryRepository.findById(stationInventoryId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Station inventory not found"));
        Station station = stationInventory.getStation();

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

        /**
     * Ensures a transaction will not produce an invalid inventory balance.
     */
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


    /**
     * Computes the new running inventory balance.
     */
    // private BigDecimal calculateNewBalance(
    //         BigDecimal currentBalance,
    //         BigDecimal quantity,
    //         InventoryTransactionType transactionType
    // ) {

    //     if (isInboundTransaction(transactionType)) {

    //         return currentBalance.add(quantity);
    //     }

    //     if (isOutboundTransaction(transactionType)) {

    //         return currentBalance.subtract(quantity);
    //     }

    //     /*
    //      * STOCK_COUNT and ADJUSTMENT can either
    //      * increase or decrease inventory.
    //      *
    //      * The caller should supply:
    //      *
    //      * positive quantity  -> increase
    //      * negative quantity  -> decrease
    //      */
    //     if (transactionType == InventoryTransactionType.ADJUSTMENT
    //             || transactionType == InventoryTransactionType.STOCK_COUNT) {

    //         return currentBalance.add(quantity);
    //     }

    //     throw new BadRequestException(
    //             "Unsupported inventory transaction type: "
    //                     + transactionType
    //     );
    // }
    private BigDecimal calculateNewBalance(
        BigDecimal currentBalance,
        BigDecimal quantity,
        InventoryTransactionType transactionType
) {

    if (isInboundTransaction(transactionType)) {

        return currentBalance.add(quantity);
    }

    if (isOutboundTransaction(transactionType)) {

        return currentBalance.subtract(quantity);
    }

    throw new BadRequestException(
            "Unsupported inventory transaction type: "
                    + transactionType
    );
}


    /**
     * Transactions that increase stock.
     */
    private boolean isInboundTransaction(
            InventoryTransactionType transactionType
    ) {

        return switch (transactionType) {

            case OPENING_STOCK,
                    DELIVERY,
                    TRANSFER_IN,
                    RETURN,
                    ADJUSTMENT_IN -> true;

            default -> false;
        };
    }


    /**
     * Transactions that reduce stock.
     */
    private boolean isOutboundTransaction(
            InventoryTransactionType transactionType
    ) {

        return switch (transactionType) {

            case SALE,
                    TRANSFER_OUT,
                    LOSS,
                    ADJUSTMENT_OUT -> true;

            default -> false;
        };
    }


    /**
     * Basic quantity validation.
     */
    private void validateQuantity(
            BigDecimal quantity
    ) {

        if (quantity == null) {

            throw new BadRequestException(
                    "Quantity is required."
            );
        }

        if (quantity.compareTo(BigDecimal.ZERO) == 0) {

            throw new BadRequestException(
                    "Quantity must be greater than zero."
            );
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

            throw new BadRequestException(
                    "Quantity cannot be negative."
            );
        }
    }

}
