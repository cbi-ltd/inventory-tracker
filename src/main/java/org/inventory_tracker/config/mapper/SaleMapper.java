package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateSaleRequest;
import org.inventory_tracker.dto.response.SaleResponse;
import org.inventory_tracker.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SaleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "pump", ignore = true)
    @Mapping(target = "terminal", ignore = true)
    @Mapping(target = "attendant", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "saleNumber", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "saleTime", ignore = true)
    @Mapping(target = "shift", ignore = true)
    @Mapping(target = "grossAmount", ignore = true)
    @Mapping(target = "netAmount", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "saleStatus", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "receiptNumber", ignore = true)
    @Mapping(target = "inventoryUpdated", ignore = true)

    Sale toEntity(CreateSaleRequest request);

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    @Mapping(source = "pump.id", target = "pumpId")
    @Mapping(source = "pump.pumpNumber", target = "pumpNumber")
    @Mapping(source = "terminal.id", target = "terminalId")
    @Mapping(source = "terminal.terminalSerialNumber", target = "terminalSerialNumber")
    @Mapping(source = "attendant.id", target = "attendantId")
    @Mapping(source = "attendant.fullName", target = "attendantName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")

    SaleResponse toResponse(Sale sale);
}
