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
    StationInventory toEntity(
            CreateStationInventoryRequest request
    );

    StationInventoryResponse toResponse(
            StationInventory inventory
    );

    List<StationInventoryResponse> toResponseList(
            List<StationInventory> inventories
    );



//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "station", ignore = true)
//    StationInventory toEntity(
//            CreateStationInventoryRequest request
//    );
//
//    @Mapping(source = "station.id", target = "stationId")
//    @Mapping(source = "station.name", target = "stationName")
//    StationInventoryResponse toResponse(
//            StationInventory inventory
//    );
//
//    List<StationInventoryResponse> toResponseList(
//            List<StationInventory> inventories
//    );
}