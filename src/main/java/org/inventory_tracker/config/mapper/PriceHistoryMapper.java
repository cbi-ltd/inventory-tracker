// package org.inventory_tracker.config.mapper;


// import org.inventory_tracker.dto.response.PriceHistoryResponse;
// import org.inventory_tracker.entity.PriceHistory;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.ReportingPolicy;

// import java.util.List;

// @Mapper(
//         componentModel = "spring",
//         unmappedTargetPolicy = ReportingPolicy.IGNORE
// )
// public interface PriceHistoryMapper {

//     @Mapping(
//             source = "stationInventory.id",
//             target = "stationInventoryId"
//     )

//     @Mapping(
//             source = "stationInventory.station.id",
//             target = "stationId"
//     )

//     @Mapping(
//             source = "stationInventory.station.name",
//             target = "stationName"
//     )

//     @Mapping(
//             source = "stationInventory.product.id",
//             target = "productId"
//     )

//     @Mapping(
//             source = "stationInventory.product.name",
//             target = "productName"
//     )

//     PriceHistoryResponse toResponse(
//             PriceHistory entity);

//     List<PriceHistoryResponse> toResponseList(
//             List<PriceHistory> entities);
// }
