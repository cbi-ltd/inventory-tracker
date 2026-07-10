package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateDeliveryRequest;
import org.inventory_tracker.dto.response.DeliveryResponse;
import org.inventory_tracker.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;


@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stationInventory", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "receivedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Delivery toEntity(CreateDeliveryRequest request);

    @Mapping(source = "stationInventory.id", target = "stationInventoryId")

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")

    DeliveryResponse toResponse(Delivery delivery);

    List<DeliveryResponse> toResponseList(List<Delivery> deliveries);
}
