package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.FuelingCompanyRequest;
import org.inventory_tracker.dto.response.FuelingCompanyResponse;
import org.inventory_tracker.entity.FuelingCompany;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;


@Mapper(componentModel = "spring")
public interface FuelingCompanyMapper {

    FuelingCompanyResponse toResponse(FuelingCompany company);

    List<FuelingCompanyResponse> toResponseList(List<FuelingCompany> companies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "active", ignore = true)
    FuelingCompany toEntity(FuelingCompanyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(
            FuelingCompanyRequest request,
            @MappingTarget FuelingCompany company
    );
}
