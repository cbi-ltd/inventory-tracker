package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateInventoryTransactionRequest;
import org.inventory_tracker.dto.response.InventoryTransactionResponse;
import org.inventory_tracker.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;


@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stationInventory", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "transactionTime", ignore = true)
    @Mapping(target = "balanceBeforeTransaction", ignore = true)
    @Mapping(target = "balanceAfterTransaction", ignore = true)
    @Mapping(target = "referenceNumber", ignore = true)
    InventoryTransaction toEntity(
            CreateInventoryTransactionRequest request
    );

    @Mapping(source = "stationInventory.id", target = "stationInventoryId")

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")

    InventoryTransactionResponse toResponse(
            InventoryTransaction transaction
    );

    List<InventoryTransactionResponse> toResponseList(
            List<InventoryTransaction> transactions
    );
}
