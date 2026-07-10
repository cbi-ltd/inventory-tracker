package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateStationInventoryRequest;
import org.inventory_tracker.dto.response.StationInventoryResponse;
import org.inventory_tracker.entity.StationInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StationInventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "currentQuantity", ignore = true)
    @Mapping(target = "active", ignore = true)
    StationInventory toEntity(
            CreateStationInventoryRequest request
    );

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.productType", target = "productType")
    @Mapping(source = "product.unitOfMeasure", target = "unit")

    StationInventoryResponse toResponse(
            StationInventory inventory
    );

    List<StationInventoryResponse> toResponseList(
            List<StationInventory> inventories
    );
}