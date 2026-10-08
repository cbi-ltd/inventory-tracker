package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.VehicleRequest;
import org.inventory_tracker.dto.response.VehicleResponse;
import org.inventory_tracker.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    VehicleResponse toResponse(Vehicle vehicle);

    List<VehicleResponse> toResponseList(List<Vehicle> vehicles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "active", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(
            VehicleRequest request,
            @MappingTarget Vehicle vehicle
    );
}
