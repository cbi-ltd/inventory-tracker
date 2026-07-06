package org.inventory_tracker.config.mapper;


import org.inventory_tracker.dto.request.CreateTransactionRequest;
import org.inventory_tracker.dto.response.SaleTransactionResponse;
import org.inventory_tracker.entity.SaleTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "transactionReference",
            ignore = true
    )
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "attendant", ignore = true)
    @Mapping(target = "businessType", ignore = true)
    @Mapping(target = "posTerminal", ignore = true)
    SaleTransaction toEntity(
            CreateTransactionRequest request
    );

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")
    @Mapping(source = "attendant.id", target = "attendantId")
    @Mapping(source = "attendant.fullName", target = "attendantName")
    @Mapping(
            source = "businessType.id",
            target = "businessTypeId"
    )
    @Mapping(
            source = "businessType.name",
            target = "businessTypeName"
    )
    @Mapping(
            source = "posTerminal.id",
            target = "posTerminalId"
    )
    @Mapping(
            source = "posTerminal.serialNumber",
            target = "terminalSerialNumber"
    )
    SaleTransactionResponse toResponse(
            SaleTransaction transaction
    );

    List<SaleTransactionResponse> toResponseList(
            List<SaleTransaction> transactions
    );
}