package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateProductRequest;
import org.inventory_tracker.dto.response.ProductResponse;
import org.inventory_tracker.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.inventory_tracker.dto.request.UpdateProductRequest;
import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(UpdateProductRequest dto, @MappingTarget Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(
            List<Product> products);
}
