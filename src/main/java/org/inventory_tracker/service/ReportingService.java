package org.inventory_tracker.service;

import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.response.DashboardResponse;
import org.inventory_tracker.dto.response.DeliveryReportResponse;
import org.inventory_tracker.dto.response.ExecutiveSummaryResponse;
import org.inventory_tracker.dto.response.InventoryReportResponse;
import org.inventory_tracker.dto.response.InventoryTransactionReportResponse;
import org.inventory_tracker.dto.response.PaymentReportResponse;
import org.inventory_tracker.dto.response.PriceHistoryReportResponse;
import org.inventory_tracker.dto.response.ProductReportResponse;
import org.inventory_tracker.dto.response.PumpAssignmentReportResponse;
import org.inventory_tracker.dto.response.PumpAuditReportResponse;
import org.inventory_tracker.dto.response.PumpReportResponse;
import org.inventory_tracker.dto.response.SalesReportResponse;
import org.inventory_tracker.dto.response.StationReportResponse;
import org.inventory_tracker.dto.response.AttendantReportResponse;
import org.inventory_tracker.entity.Attendant;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAssignment;
import org.inventory_tracker.entity.Sale;
import org.inventory_tracker.entity.Station;
import org.inventory_tracker.entity.Terminal;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.DeliveryRepository;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.ProductPriceHistoryRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.PaymentRepository;
import org.inventory_tracker.repository.InventoryTransactionRepository;
import org.inventory_tracker.repository.SaleRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingService {

    private final StationRepository stationRepository;
    private final ProductRepository productRepository;
    private final PumpRepository pumpRepository;
    private final PumpAuditRepository pumpAuditRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final AttendantRepository attendantRepository;
    private final DeliveryRepository deliveryRepository;
    private final StationInventoryRepository stationInventoryRepository;
    private final SaleRepository saleRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;

    public DashboardResponse getDashboard() {

        LocalDate businessDate = LocalDate.now();

        BigDecimal totalInventoryQuantity =
                calculateTotalInventoryQuantity();

        BigDecimal totalInventoryValue =
                calculateTotalInventoryValue();

        return DashboardResponse.builder()

                .businessDate(businessDate)

                .totalStations(
                        stationRepository.count()
                )

                .totalProducts(
                        productRepository.count()
                )

                .totalPumps(
                        pumpRepository.count()
                )

                .totalAttendants(
                        attendantRepository.count()
                )

                .activePumpAssignments(
                        pumpAssignmentRepository.countByActiveTrue()
                )

                .totalDeliveriesToday(
                        deliveryRepository.countByBusinessDate(
                                businessDate)
                )

                // .totalTransfersToday(
                //         stockTransferRepository.countByBusinessDate(
                //                 businessDate)
                // )

                // .totalAdjustmentsToday(
                //         stockAdjustmentRepository.countByBusinessDate(
                //                 businessDate)
                // )

                // .totalStockCountsToday(
                //         stockCountRepository.countByBusinessDate(
                //                 businessDate)
                // )

                .totalInventoryQuantity(
                        totalInventoryQuantity
                )

                .totalInventoryValue(
                        totalInventoryValue
                )

                .lowStockProducts(
                        stationInventoryRepository
                                .countByCurrentQuantityLessThanEqualReorderLevel()
                )

                .lowStockStations(
                        stationInventoryRepository
                                .countDistinctStationsWithLowStock()
                )

                .build();
    }

    @Transactional(readOnly = true)
    public ExecutiveSummaryResponse getExecutiveSummary() {

        LocalDate businessDate = LocalDate.now();

        return ExecutiveSummaryResponse.builder()

                .businessDate(businessDate)

                .totalStations(
                        stationRepository.count())

                .totalProducts(
                        productRepository.count())

                .totalPumps(
                        pumpRepository.count())

                .totalAttendants(
                        attendantRepository.count())

                .totalInventoryQuantity(
                        calculateTotalInventoryQuantity())

                .totalInventoryValue(
                        calculateTotalInventoryValue())

                .deliveriesToday(
                        deliveryRepository.countByBusinessDate(
                                businessDate))

                // .transfersToday(
                //         stockTransferRepository.countByBusinessDate(
                //                 businessDate))

                // .adjustmentsToday(
                //         stockAdjustmentRepository.countByBusinessDate(
                //                 businessDate))

                // .stockCountsToday(
                //         stockCountRepository.countByBusinessDate(
                //                 businessDate))

                .lowStockProducts(
                        stationInventoryRepository
                                .countByCurrentQuantityLessThanEqualReorderLevel())

                .lowStockStations(
                        stationInventoryRepository
                                .countDistinctStationsWithLowStock())

                .build();
    }

    @Transactional(readOnly = true)
    public List<StationReportResponse> getStationReport() {

        return stationRepository.findAll()

                .stream()

                .map(station -> StationReportResponse.builder()

                        .stationId(
                                station.getId())

                        .stationName(
                                station.getName())

                        .totalProducts(
                                stationInventoryRepository
                                        .countByStationId(
                                                station.getId()))

                        .totalPumps(
                                pumpRepository
                                        .countByStationId(
                                                station.getId()))

                        .activePumpAssignments(
                                pumpAssignmentRepository
                                        .countByStationIdAndActiveTrue(
                                                station.getId()))

                        .inventoryQuantity(
                                calculateStationInventoryQuantity(
                                        station.getId()))

                        .inventoryValue(
                                calculateStationInventoryValue(
                                        station.getId()))

                        .deliveries(
                                deliveryRepository
                                        .countByStationId(
                                                station.getId()))

                        // .transfersIn(
                        //         stockTransferRepository
                        //                 .countByDestinationStationId(
                        //                         station.getId()))

                        // .transfersOut(
                        //         stockTransferRepository
                        //                 .countBySourceStationId(
                        //                         station.getId()))

                        // .adjustments(
                        //         stockAdjustmentRepository
                        //                 .countByStationId(
                        //                         station.getId()))

                        // .stockCounts(
                        //         stockCountRepository
                        //                 .countByStationId(
                        //                         station.getId()))

                        .lowStockProducts(
                                stationInventoryRepository
                                        .countLowStockProductsByStation(
                                                station.getId()))

                        .build())

                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryReportResponse> getInventoryReport() {

        return stationInventoryRepository.findAll()

                .stream()

                .map(inventory -> {

                    BigDecimal inventoryValue =
                            inventory.getCurrentQuantity()
                                    .multiply(
                                            inventory.getSellingPrice());

                    boolean belowReorder =
                            inventory.getCurrentQuantity()
                                    .compareTo(
                                            inventory.getReorderLevel()) <= 0;

                    return InventoryReportResponse.builder()

                            .stationId(
                                    inventory.getStation().getId())

                            .stationName(
                                    inventory.getStation().getName())

                            .productId(
                                    inventory.getProduct().getId())

                            .productName(
                                    inventory.getProduct().getName())

                            .currentQuantity(
                                    inventory.getCurrentQuantity())

                            .sellingPrice(
                                    inventory.getSellingPrice())

                            .inventoryValue(
                                    inventoryValue)

                            .reorderLevel(
                                    inventory.getReorderLevel())

                            .belowReorderLevel(
                                    belowReorder)

                            .build();

                })

                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductReportResponse> getProductReport() {

        return productRepository.findAll().stream()
                .map(product -> ProductReportResponse.builder()
                        .productId(product.getId())
                        .productName(product.getName())

                        .productCode(product.getProductType() != null ? product.getProductType().getCode(): null)

                        .stationsStockingProduct(stationInventoryRepository
                                        .countDistinctStationsByProductId(product.getId()))

                        .totalQuantity(calculateProductQuantity(product.getId()))

                        .averageSellingPrice(calculateAverageSellingPrice(product.getId()))

                        .inventoryValue(calculateProductInventoryValue(product.getId()))

                        .deliveries(deliveryRepository.countByProductId(product.getId()))

                        // .transfers(stockTransferRepository.countByProductId(product.getId()))

                        // .adjustments(stockAdjustmentRepository.countByProductId(product.getId()))

                        // .stockCounts(stockCountRepository.countByProductId(product.getId()))

                        .build())

                .toList();
    }

    @Transactional(readOnly = true)
    public List<PumpReportResponse> getPumpReport() {

        return pumpRepository.findAll()

                .stream()

                .map(pump -> PumpReportResponse.builder()

                        .pumpId(
                                pump.getId())

                        .pumpNumber(
                                pump.getPumpNumber())

                        .pumpName(
                                pump.getPumpName())

                        .stationName(
                                pump.getStation().getName())

                        .productName(
                                pump.getProduct().getName())

                        .active(
                                pump.getActive())

                        .totalAssignments(
                                pumpAssignmentRepository
                                        .countByPumpId(
                                                pump.getId()))

                        .activeAssignments(
                                pumpAssignmentRepository
                                        .countByPumpIdAndActiveTrue(
                                                pump.getId()))

                        .auditsCompleted(0L)

                        .build())

                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendantReportResponse> getAttendantReport() {

        return attendantRepository.findAll().stream()
                .map(attendant -> {
                    PumpAssignment assignment =
                            pumpAssignmentRepository.findFirstByAttendantIdAndActiveTrue(attendant.getId())
                                    .orElse(null);

                    String assignedPump =assignment == null? null
                                    : assignment.getPump().getPumpNumber();

                    return AttendantReportResponse.builder()
                            .attendantId(attendant.getId())
                            .username(attendant.getUsername())
                            .fullName(attendant.getFullName())
                            .stationName(attendant.getStation() != null
                                            ? attendant.getStation().getName(): null)
                            .assignedPump(assignedPump)
                            .active(attendant.getActive())
                            .completedShifts(0L)
                        //     .stockCountsPerformed(stockCountRepository.countByCountedBy(attendant.getUsername()))
                        //     .adjustmentsPerformed(stockAdjustmentRepository.countByAdjustedBy(attendant.getUsername()))
                            .deliveriesReceived(0L)
                        //     .deliveriesReceived(deliveryRepository.countByReceivedBy(attendant.getUsername()))
                        //     .transfersInitiated(stockTransferRepository.countByInitiatedBy(attendant.getUsername()))
                            .build();
                })
                .toList();
    }


    private BigDecimal calculateTotalInventoryQuantity() {

        return stationInventoryRepository.findAll()

                .stream()

                .map(StationInventory::getCurrentQuantity)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateTotalInventoryValue() {

        return stationInventoryRepository.findAll()

                .stream()

                .map(inventory ->

                        inventory.getCurrentQuantity()

                                .multiply(
                                        inventory.getSellingPrice()))

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateStationInventoryQuantity(Long stationId) {

        return stationInventoryRepository

                .findByStationId(stationId)

                .stream()

                .map(StationInventory::getCurrentQuantity)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateStationInventoryValue(Long stationId) {

        return stationInventoryRepository

                .findByStationId(stationId)

                .stream()

                .map(inventory ->

                        inventory.getCurrentQuantity()

                                .multiply(
                                        inventory.getSellingPrice()))

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateProductQuantity(Long productId) {

        return stationInventoryRepository

                .findByProductId(productId)

                .stream()

                .map(StationInventory::getCurrentQuantity)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateProductInventoryValue(Long productId) {

        return stationInventoryRepository

                .findByProductId(productId)

                .stream()

                .map(inventory ->

                        inventory.getCurrentQuantity()

                                .multiply(
                                        inventory.getSellingPrice()))

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    private BigDecimal calculateAverageSellingPrice(Long productId) {

        List<StationInventory> inventories =

                stationInventoryRepository
                        .findByProductId(productId);

        if (inventories.isEmpty()) {

            return BigDecimal.ZERO;
        }

        BigDecimal total = inventories.stream()

                .map(StationInventory::getSellingPrice)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        return total.divide(

                BigDecimal.valueOf(
                        inventories.size()),

                2,

                RoundingMode.HALF_UP
        );
    }

    @Transactional(readOnly = true)
    public List<PumpAssignmentReportResponse> getPumpAssignmentReport() {
        return pumpAssignmentRepository.findAll()
                .stream()
                .map(assignment -> {
                        Pump pump = assignment.getPump();
                        Attendant attendant = assignment.getAttendant();
                        Station station = assignment.getStation();
                        Terminal terminal = assignment.getTerminal();

                        return PumpAssignmentReportResponse.builder()
                                .assignmentId(assignment.getId())
                                .stationId(station != null ? station.getId() : null)
                                .stationName(station != null ? station.getName() : null)
                                .pumpId(pump != null ? pump.getId(): null)
                                .pumpNumber(pump != null ? pump.getPumpNumber() : null)
                                .pumpName(pump != null ? pump.getPumpName() : null)
                                .attendantId(attendant != null ? attendant.getId() : null)
                                .attendantName(attendant != null ? attendant.getFullName(): null)
                                .terminalId(terminal != null ? terminal.getId(): null)
                                .terminalSerialNumber(terminal != null ? terminal.getTerminalSerialNumber(): null)
                                .assignmentDate(assignment.getAssignmentDate())
                                .shift(assignment.getShift())
                                .active(assignment.getActive())
                                .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
public List<PumpAuditReportResponse> getPumpAuditReport() {

    return pumpAuditRepository
            .findAllByOrderByBusinessDateDesc()
            .stream()
            .map(audit -> {

                PumpAssignment assignment =
                        audit.getPumpAssignment();

                Pump pump =
                        assignment != null
                                ? assignment.getPump()
                                : null;

                Attendant attendant =
                        assignment != null
                                ? assignment.getAttendant()
                                : null;

                Station station =
                        assignment != null
                                ? assignment.getStation()
                                : null;

                return PumpAuditReportResponse.builder()

                        .auditId(
                                audit.getId())

                        .assignmentId(
                                assignment != null
                                        ? assignment.getId()
                                        : null)

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .pumpId(
                                pump != null
                                        ? pump.getId()
                                        : null)

                        .pumpNumber(
                                pump != null
                                        ? pump.getPumpNumber()
                                        : null)

                        .pumpName(
                                pump != null
                                        ? pump.getPumpName()
                                        : null)

                        .attendantId(
                                attendant != null
                                        ? attendant.getId()
                                        : null)

                        .attendantName(
                                attendant != null
                                        ? attendant.getFullName()
                                        : null)

                        .businessDate(
                                audit.getBusinessDate())

                        .shift(
                                assignment != null
                                        ? assignment.getShift()
                                        : null)

                        .clockInTime(
                                audit.getClockInTime())

                        .clockOutTime(
                                audit.getClockOutTime())

                        .openingReading(
                                audit.getOpeningReading())

                        .closingReading(
                                audit.getClosingReading())

                        .totalDispensed(
                                audit.getTotalDispensed())

                        .build();
            })
            .toList();
}

@Transactional(readOnly = true)
public List<SalesReportResponse> getSalesReport() {

    return saleRepository
            .findAllByOrderByBusinessDateDescSaleTimeDesc()
            .stream()

            .map(sale -> {

                Station station =
                        sale.getStation();

                Pump pump =
                        sale.getPump();

                Product product =
                        sale.getProduct();

                Attendant attendant =
                        sale.getAttendant();

                 return SalesReportResponse.builder()

                        .saleId(
                                sale.getId())

                        .saleNumber(
                                sale.getSaleNumber())

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .pumpId(
                                pump != null
                                        ? pump.getId()
                                        : null)

                        .pumpNumber(
                                pump != null
                                        ? pump.getPumpNumber()
                                        : null)

                        .pumpName(
                                pump != null
                                        ? pump.getPumpName()
                                        : null)

                        .productId(
                                product != null
                                        ? product.getId()
                                        : null)

                        .productName(
                                product != null
                                        ? product.getName()
                                        : null)

                        .attendantId(
                                attendant != null
                                        ? attendant.getId()
                                        : null)

                        .attendantName(
                                attendant != null
                                        ? attendant.getFullName()
                                        : null)

                        .businessDate(
                                sale.getBusinessDate())

                        .saleTime(
                                sale.getSaleTime())

                        .shift(
                                sale.getShift())

                        .quantity(
                                sale.getQuantity())

                        .unitPrice(
                                sale.getSellingPrice())

                        .grossAmount(
                                sale.getGrossAmount())

                        .discountAmount(
                                sale.getDiscountAmount())

                        .netAmount(
                                sale.getNetAmount())

                        .paymentMethod(
                                sale.getPaymentMethod())

                        .paymentStatus(
                                sale.getPaymentStatus())

                        .saleStatus(
                                sale.getSaleStatus())

                        .transactionReference(
                                sale.getTransactionReference())

                        .build();
            })
            .toList();
}

@Transactional(readOnly = true)
public List<DeliveryReportResponse> getDeliveryReport() {

    return deliveryRepository
            .findAllByOrderByBusinessDateDescReceivedAtDesc()
            .stream()

            .map(delivery -> {

                Station station =
                        delivery.getStation();

                Product product =
                        delivery.getProduct();

                StationInventory stationInventory =
                        delivery.getStationInventory();

                BigDecimal totalCost = BigDecimal.ZERO;

                if (delivery.getQuantityDelivered() != null
                        && delivery.getCostPerUnit() != null) {

                    totalCost =
                            delivery.getQuantityDelivered()
                                    .multiply(
                                            delivery.getCostPerUnit());
                }

                return DeliveryReportResponse.builder()

                        .deliveryId(
                                delivery.getId())

                        .deliveryNumber(
                                delivery.getDeliveryNumber())

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .productId(
                                product != null
                                        ? product.getId()
                                        : null)

                        .productName(
                                product != null
                                        ? product.getName()
                                        : null)

                        .stationInventoryId(
                                stationInventory != null
                                        ? stationInventory.getId()
                                        : null)

                        .quantityDelivered(
                                delivery.getQuantityDelivered())

                        .costPerUnit(
                                delivery.getCostPerUnit())

                        .totalCost(
                                totalCost)

                        .status(
                                delivery.getStatus())

                        .businessDate(
                                delivery.getBusinessDate())

                        .receivedAt(
                                delivery.getReceivedAt())

                        .reversedAt(
                                delivery.getReversedAt())

                        .remarks(
                                delivery.getRemarks())

                        .reversalReason(
                                delivery.getReversalReason())

                        .build();
            })
            .toList();
}

@Transactional(readOnly = true)
public List<PaymentReportResponse> getPaymentReport() {

    return paymentRepository
            .findAll()
            .stream()
            .map(payment -> {

                Sale sale = payment.getSale();

                Station station =
                        sale != null
                                ? sale.getStation()
                                : null;

                Pump pump =
                        sale != null
                                ? sale.getPump()
                                : null;

                Attendant attendant =
                        sale != null
                                ? sale.getAttendant()
                                : null;

                return PaymentReportResponse.builder()

                        .paymentId(
                                payment.getId())

                        .saleId(
                                sale != null
                                        ? sale.getId()
                                        : null)

                        .saleNumber(
                                sale != null
                                        ? sale.getSaleNumber()
                                        : null)

                        .transactionReference(
                                sale != null
                                        ? sale.getTransactionReference()
                                        : null)

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .pumpId(
                                pump != null
                                        ? pump.getId()
                                        : null)

                        .pumpNumber(
                                pump != null
                                        ? pump.getPumpNumber()
                                        : null)

                        .attendantId(
                                attendant != null
                                        ? attendant.getId()
                                        : null)

                        .attendantName(
                                attendant != null
                                        ? attendant.getFullName()
                                        : null)

                        .amount(
                                payment.getAmount())

                        .paymentMethod(
                                payment.getPaymentMethod())

                        .paymentStatus(
                                payment.getPaymentStatus())

                        .businessDate(
                                sale != null
                                        ? sale.getBusinessDate()
                                        : null)

                        .paymentTime(
                                payment.getPaymentTime())

                        .build();
            })
            .toList();
}

@Transactional(readOnly = true)
public List<InventoryTransactionReportResponse> getInventoryTransactionReport() {

    return inventoryTransactionRepository
            .findAll()
            .stream()
            .map(transaction -> {

                Station station =
                        transaction.getStation();

                Product product =
                        transaction.getProduct();

                StationInventory stationInventory =
                        transaction.getStationInventory();

                return InventoryTransactionReportResponse.builder()

                        .transactionId(
                                transaction.getId())

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .stationInventoryId(
                                stationInventory != null
                                        ? stationInventory.getId()
                                        : null)

                        .productId(
                                product != null
                                        ? product.getId()
                                        : null)

                        .productName(
                                product != null
                                        ? product.getName()
                                        : null)

                        .transactionType(
                                transaction.getTransactionType())

                        .quantity(
                                transaction.getQuantity())

                        .balanceBeforeTransaction(
                                transaction.getBalanceBeforeTransaction())

                        .balanceAfterTransaction(
                                transaction.getBalanceAfterTransaction())

                        .remarks(
                                transaction.getRemarks())

                        .referenceNumber(
                                transaction.getReferenceNumber())

                        .businessDate(
                                transaction.getBusinessDate())

                        .transactionTime(
                                transaction.getTransactionTime())

                        .build();
            })
            .toList();
}

@Transactional(readOnly = true)
public List<PriceHistoryReportResponse> getPriceHistoryReport() {

    return productPriceHistoryRepository
            .findAll(
                    Sort.by(
                            Sort.Direction.DESC,
                            "changedAt"))
            .stream()
            .map(history -> {

                Station station =
                        history.getStation();

                Product product =
                        history.getProduct();

                return PriceHistoryReportResponse.builder()

                        .historyId(
                                history.getId())

                        .stationId(
                                station != null
                                        ? station.getId()
                                        : null)

                        .stationName(
                                station != null
                                        ? station.getName()
                                        : null)

                        .productId(
                                product != null
                                        ? product.getId()
                                        : null)

                        .productName(
                                product != null
                                        ? product.getName()
                                        : null)

                        .oldSellingPrice(
                                history.getOldPrice())

                        .newSellingPrice(
                                history.getNewPrice())

                        .priceDifference(
                                history.getPriceDifference())

                        .changedBy(
                                history.getChangedBy())

                        .businessDate(
                                history.getBusinessDate())

                        .changedAt(
                                history.getChangedAt())

                        .build();
            })
            .toList();
}

}