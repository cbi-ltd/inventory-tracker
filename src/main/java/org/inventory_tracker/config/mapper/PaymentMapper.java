package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.response.PaymentResponse;
import org.inventory_tracker.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(source = "sale.id", target = "saleId")
    @Mapping(source = "sale.saleNumber", target = "saleNumber")
    @Mapping(source = "sale.terminal.id", target = "terminalId")
    @Mapping(source = "sale.terminal.terminalSerialNumber", target = "terminalSerialNumber")
    @Mapping(source = "sale.quantity", target = "quantitySold")
    @Mapping(source = "sale.pump.pumpName", target = "pumpName")
    @Mapping(source = "sale.pump.pumpNumber", target = "pumpNumber")
    @Mapping(source = "sale.attendant.fullName", target = "attendantName")
    @Mapping(source = "sale.station.id", target = "stationId")
    @Mapping(source = "sale.station.name", target = "stationName")
    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> payments);
}
