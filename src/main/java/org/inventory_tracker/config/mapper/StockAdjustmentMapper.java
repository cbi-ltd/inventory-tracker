package org.inventory_tracker.config.mapper;


import org.inventory_tracker.dto.request.CreateStockAdjustmentRequest;
import org.inventory_tracker.dto.response.StockAdjustmentResponse;
import org.inventory_tracker.entity.StockAdjustment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockAdjustmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adjustmentNumber", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "stationInventory", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "stockCount", ignore = true)
    @Mapping(target = "balanceBeforeAdjustment", ignore = true)
    @Mapping(target = "balanceAfterAdjustment", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "totalAdjustmentValue", ignore = true)
    StockAdjustment toEntity(
            CreateStockAdjustmentRequest request
    );

    @Mapping(source = "stationInventory.id", target = "stationInventoryId")
    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "stockCount.id", target = "stockCountId")
    @Mapping(source = "stockCount.countNumber", target = "stockCountNumber")
    StockAdjustmentResponse toResponse(
            StockAdjustment adjustment
    );

    List<StockAdjustmentResponse> toResponseList(
            List<StockAdjustment> adjustments
    );
}
