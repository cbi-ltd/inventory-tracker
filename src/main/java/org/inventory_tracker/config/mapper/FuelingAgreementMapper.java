package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.response.FuelingAgreementResponse;
import org.inventory_tracker.entity.FuelingAgreement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;



@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FuelingAgreementMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "merchant.id", target = "merchantId")
    FuelingAgreementResponse toResponse(FuelingAgreement agreement);

    List<FuelingAgreementResponse> toResponseList(
            List<FuelingAgreement> agreements);
}
