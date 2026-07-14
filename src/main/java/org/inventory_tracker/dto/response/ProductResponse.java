package org.inventory_tracker.dto.response;

import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.UnitOfMeasure;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private ProductType productType;

    private String productCode;

    private String productDescription;

    private UnitOfMeasure unitOfMeasure;

    private Boolean active;

    private String description;
}
