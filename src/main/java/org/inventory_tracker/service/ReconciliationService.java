// package org.inventory_tracker.service;

// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.dto.ReconciliationReport;
// import org.inventory_tracker.entity.PumpAudit;
// import org.inventory_tracker.repository.FuelSaleRepository;
// import org.inventory_tracker.repository.PumpAuditRepository;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class ReconciliationService {

//     private final PumpAuditRepository pumpAuditRepository;

//     private final FuelSaleRepository fuelSaleRepository;

//     public ReconciliationReport reconcile(
//             Long pumpAuditId
//     ) {

//         PumpAudit audit =
//                 pumpAuditRepository
//                         .findById(pumpAuditId)
//                         .orElseThrow(
//                                 () -> new RuntimeException(
//                                         "Pump audit not found"
//                                 )
//                         );

//         double actualDispensed =
//                 audit.getClosingReading()
//                         - audit.getOpeningReading();

//         double salesLitres =
//                 fuelSaleRepository
//                         .sumLitresByPumpAudit(
//                                 pumpAuditId
//                         );

//         double salesRevenue =
//                 fuelSaleRepository
//                         .sumRevenueByPumpAudit(
//                                 pumpAuditId
//                         );

//         double volumeVariance =
//                 actualDispensed
//                         - salesLitres;

//         double expectedRevenue =
//                 actualDispensed
//                         * audit.getPump()
//                         .getBusinessType()
//                         .getStationPricePerUnit();

//         double revenueVariance =
//                 expectedRevenue
//                         - salesRevenue;

//         return ReconciliationReport
//                 .builder()
//                 .merchantId(
//                         audit.getMerchantId()
//                 )
//                 .outletId(
//                         audit.getOutletId()
//                 )
//                 .pumpAuditId(
//                         audit.getId()
//                 )
//                 .openingReading(
//                         audit.getOpeningReading()
//                 )
//                 .closingReading(
//                         audit.getClosingReading()
//                 )
//                 .actualDispensedLitres(
//                         actualDispensed
//                 )
//                 .recordedSalesLitres(
//                         salesLitres
//                 )
//                 .volumeVariance(
//                         volumeVariance
//                 )
//                 .expectedRevenue(
//                         expectedRevenue
//                 )
//                 .actualRevenue(
//                         salesRevenue
//                 )
//                 .revenueVariance(
//                         revenueVariance
//                 )
//                 .volumeVarianceDetected(
//                         volumeVariance != 0
//                 )
//                 .revenueVarianceDetected(
//                         revenueVariance != 0
//                 )
//                 .build();
//     }
// }
