package org.inventory_tracker.config.mapper;

import org.inventory_tracker.dto.request.CreateProductPriceHistoryRequest;
import org.inventory_tracker.dto.response.ProductPriceHistoryResponse;
import org.inventory_tracker.entity.ProductPriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;



@Mapper(componentModel = "spring")
public interface ProductPriceHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "businessDate", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    ProductPriceHistory toEntity(
            CreateProductPriceHistoryRequest request
    );

    @Mapping(source = "station.id", target = "stationId")
    @Mapping(source = "station.name", target = "stationName")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.productType", target = "productType")

    ProductPriceHistoryResponse toResponse(
            ProductPriceHistory history
    );

    List<ProductPriceHistoryResponse> toResponseList(
            List<ProductPriceHistory> histories
    );
}
