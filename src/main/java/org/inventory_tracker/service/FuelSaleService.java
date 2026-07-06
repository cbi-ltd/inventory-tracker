package org.inventory_tracker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateFuelSaleRequest;
import org.inventory_tracker.entity.FuelSale;
import org.inventory_tracker.entity.Pump;
import org.inventory_tracker.entity.PumpAudit;
import org.inventory_tracker.repository.FuelSaleRepository;
import org.inventory_tracker.repository.PumpAuditRepository;
import org.inventory_tracker.repository.PumpRepository;
import org.springframework.stereotype.Service;
import org.inventory_tracker.enums.TransactionStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class FuelSaleService {

    private final FuelSaleRepository fuelSaleRepository;

    private final PumpRepository pumpRepository;

    private final PumpAuditRepository pumpAuditRepository;

    private final StationInventoryService
            stationInventoryService;

    public FuelSale createSale(
            CreateFuelSaleRequest request
    ) {

        Pump pump =
                pumpRepository
                        .findById(
                                request.getPumpId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Pump not found"
                                )
                        );

        PumpAudit audit =
                pumpAuditRepository
                        .findById(
                                request.getPumpAuditId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Pump audit not found"
                                )
                        );

        double price =
                pump.getBusinessType()
                        .getStationPricePerUnit();

        double totalAmount =
                request.getLitres()
                        * price;

        FuelSale sale =
                new FuelSale();

        sale.setPump(
                pump
        );

        sale.setPumpAudit(
                audit
        );

        sale.setProductType(
                pump.getProductType()
        );

        sale.setLitres(
                request.getLitres()
        );

        sale.setPricePerLitre(
                price
        );

        sale.setTotalAmount(
                totalAmount
        );

        sale.setMerchantId(
                request.getMerchantId()
        );

        sale.setOutletId(
                request.getOutletId()
        );

        sale.setAttendantId(
                request.getAttendantId()
        );

        sale.setPaymentMethod(
                request.getPaymentMethod()
        );

        sale.setTransactionReference(
                request.getTransactionReference()
        );

        sale.setStatus(
                TransactionStatus.SUCCESS
        );

        FuelSale saved =
                fuelSaleRepository
                        .save(
                                sale
                        );

        stationInventoryService
                .updateAfterSale(
                        request.getOutletId(),
                        sale.getProductType(),
                        sale.getLitres()
                );

        return saved;
    }
}
