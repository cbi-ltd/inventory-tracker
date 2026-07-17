package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "sale.id", target = "saleId")
    @Mapping(source = "sale.saleNumber", target = "saleNumber")
    @Mapping(source = "terminal.id", target = "terminalId")
    @Mapping(source = "terminal.terminalSerialNumber", target = "terminalSerialNumber")

    PaymentResponse toResponse(Payment payment);
}
