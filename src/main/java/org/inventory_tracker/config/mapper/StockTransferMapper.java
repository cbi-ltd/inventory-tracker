package org.inventory_tracker.config.mapper;


import org.inventory_tracker.dto.request.CreateStockTransferRequest;
import org.inventory_tracker.dto.response.StockTransferResponse;
import org.inventory_tracker.entity.StockTransfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockTransferMapper {

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "transferNumber", ignore = true)

    @Mapping(target = "sourceInventory", ignore = true)

    @Mapping(target = "destinationInventory", ignore = true)

    @Mapping(target = "sourceStation", ignore = true)

    @Mapping(target = "destinationStation", ignore = true)

    @Mapping(target = "product", ignore = true)

    @Mapping(target = "status", ignore = true)

    @Mapping(target = "businessDate", ignore = true)

    @Mapping(target = "completedAt", ignore = true)

    @Mapping(target = "initiatedBy", ignore = true)

    @Mapping(target = "completedBy", ignore = true)

    StockTransfer toEntity(
            CreateStockTransferRequest request
    );


    @Mapping(source = "sourceInventory.id", target = "sourceInventoryId")

    @Mapping(source = "destinationInventory.id", target = "destinationInventoryId")


    @Mapping(source = "sourceStation.id", target = "sourceStationId")

    @Mapping(source = "sourceStation.name", target = "sourceStationName")


    @Mapping(source = "destinationStation.id", target = "destinationStationId")

    @Mapping(source = "destinationStation.name", target = "destinationStationName")


    @Mapping(source = "product.id", target = "productId")

    @Mapping(source = "product.name", target = "productName")

    StockTransferResponse toResponse(
            StockTransfer stockTransfer
    );


    List<StockTransferResponse> toResponseList(
            List<StockTransfer> stockTransfers
    );

}
