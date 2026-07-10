package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateStockCountRequest;
import org.inventory_tracker.dto.response.StockCountResponse;
import org.inventory_tracker.entity.StockCount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockCountMapper {

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "countNumber", ignore = true)

    @Mapping(target = "businessDate", ignore = true)

    @Mapping(target = "stationInventory", ignore = true)

    @Mapping(target = "station", ignore = true)

    @Mapping(target = "product", ignore = true)

    @Mapping(target = "systemQuantity", ignore = true)

    @Mapping(target = "variance", ignore = true)

    @Mapping(target = "stockAdjustment", ignore = true)

    StockCount toEntity(
            CreateStockCountRequest request
    );


    @Mapping(source = "stationInventory.id",
            target = "stationInventoryId")

    @Mapping(source = "station.id",
            target = "stationId")

    @Mapping(source = "station.name",
            target = "stationName")

    @Mapping(source = "product.id",
            target = "productId")

    @Mapping(source = "product.name",
            target = "productName")

    @Mapping(source = "stockAdjustment.id", target = "stockAdjustmentId")

    @Mapping(source = "stockAdjustment.adjustmentNumber", target = "stockAdjustmentNumber")

    StockCountResponse toResponse(
            StockCount stockCount
    );

    List<StockCountResponse> toResponseList(
            List<StockCount> stockCounts
    );
}
