package org.inventory_tracker.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.UnitOfMeasure;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Product type is required")
    private ProductType productType;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

    private String description;
}
