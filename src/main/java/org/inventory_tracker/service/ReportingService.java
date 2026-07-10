package org.inventory_tracker.service;

package org.inventory_tracker.service;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.response.DashboardResponse;
import org.inventory_tracker.dto.response.ExecutiveSummaryResponse;
import org.inventory_tracker.dto.response.InventoryReportResponse;
import org.inventory_tracker.dto.response.ProductReportResponse;
import org.inventory_tracker.dto.response.PumpReportResponse;
import org.inventory_tracker.dto.response.StationReportResponse;
import org.inventory_tracker.dto.response.AttendantReportResponse;
import org.inventory_tracker.entity.StationInventory;
import org.inventory_tracker.repository.AttendantRepository;
import org.inventory_tracker.repository.DeliveryRepository;
import org.inventory_tracker.repository.ProductRepository;
import org.inventory_tracker.repository.PumpAssignmentRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.inventory_tracker.repository.StationInventoryRepository;
import org.inventory_tracker.repository.StationRepository;
import org.inventory_tracker.repository.StockAdjustmentRepository;
import org.inventory_tracker.repository.StockCountRepository;
import org.inventory_tracker.repository.StockTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingService {

    private final StationRepository stationRepository;
    private final ProductRepository productRepository;
    private final PumpRepository pumpRepository;
    private final PumpAssignmentRepository pumpAssignmentRepository;
    private final AttendantRepository attendantRepository;
    private final DeliveryRepository deliveryRepository;
    private final StockTransferRepository stockTransferRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockCountRepository stockCountRepository;
    private final StationInventoryRepository stationInventoryRepository;

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

                .totalTransfersToday(
                        stockTransferRepository.countByBusinessDate(
                                businessDate)
                )

                .totalAdjustmentsToday(
                        stockAdjustmentRepository.countByBusinessDate(
                                businessDate)
                )

                .totalStockCountsToday(
                        stockCountRepository.countByBusinessDate(
                                businessDate)
                )

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

                .transfersToday(
                        stockTransferRepository.countByBusinessDate(
                                businessDate))

                .adjustmentsToday(
                        stockAdjustmentRepository.countByBusinessDate(
                                businessDate))

                .stockCountsToday(
                        stockCountRepository.countByBusinessDate(
                                businessDate))

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

                        .transfersIn(
                                stockTransferRepository
                                        .countByDestinationStationId(
                                                station.getId()))

                        .transfersOut(
                                stockTransferRepository
                                        .countBySourceStationId(
                                                station.getId()))

                        .adjustments(
                                stockAdjustmentRepository
                                        .countByStationId(
                                                station.getId()))

                        .stockCounts(
                                stockCountRepository
                                        .countByStationId(
                                                station.getId()))

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

        return productRepository.findAll()

                .stream()

                .map(product -> ProductReportResponse.builder()

                        .productId(
                                product.getId())

                        .productName(
                                product.getName())

                        .productCode(
                                product.getProductCode())

                        .stationsStockingProduct(
                                stationInventoryRepository
                                        .countDistinctStationsByProductId(
                                                product.getId()))

                        .totalQuantity(
                                calculateProductQuantity(
                                        product.getId()))

                        .averageSellingPrice(
                                calculateAverageSellingPrice(
                                        product.getId()))

                        .inventoryValue(
                                calculateProductInventoryValue(
                                        product.getId()))

                        .deliveries(
                                deliveryRepository
                                        .countByProductId(
                                                product.getId()))

                        .transfers(
                                stockTransferRepository
                                        .countByProductId(
                                                product.getId()))

                        .adjustments(
                                stockAdjustmentRepository
                                        .countByProductId(
                                                product.getId()))

                        .stockCounts(
                                stockCountRepository
                                        .countByProductId(
                                                product.getId()))

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

        return attendantRepository.findAll()

                .stream()

                .map(attendant -> AttendantReportResponse.builder()

                        .attendantId(
                                attendant.getId())

                        .username(
                                attendant.getUsername())

                        .fullName(
                                attendant.getFullName())

                        .stationName(

                                attendant.getStation() != null

                                        ? attendant.getStation().getName()

                                        : null

                        )

                        .assignedPump(

                                attendant.getPumpAssignment() != null

                                        ? attendant.getPumpAssignment()
                                                .getPump()
                                                .getPumpNumber()

                                        : null

                        )

                        .active(
                                attendant.getActive())

                        .completedShifts(0L)

                        .stockCountsPerformed(
                                stockCountRepository
                                        .countByCountedBy(
                                                attendant.getUsername()))

                        .adjustmentsPerformed(
                                stockAdjustmentRepository
                                        .countByAdjustedBy(
                                                attendant.getUsername()))

                        .deliveriesReceived(
                                deliveryRepository
                                        .countByReceivedBy(
                                                attendant.getUsername()))

                        .transfersInitiated(
                                stockTransferRepository
                                        .countByInitiatedBy(
                                                attendant.getUsername()))

                        .build())

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

}