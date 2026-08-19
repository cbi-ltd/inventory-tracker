package org.inventory_tracker.config;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.enums.UnitOfMeasure;
import org.inventory_tracker.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {

        createProduct(
                ProductType.PETROL,
                UnitOfMeasure.LITRE
        );

        createProduct(
                ProductType.DIESEL,
                UnitOfMeasure.LITRE
        );

        createProduct(
                ProductType.COOKING_GAS,
                UnitOfMeasure.KG
        );

        createProduct(
                ProductType.JET_FUEL,
                UnitOfMeasure.LITRE
        );

        createProduct(
                ProductType.KEROSENE,
                UnitOfMeasure.LITRE
        );

        createProduct(
                ProductType.ENGINE_OIL,
                UnitOfMeasure.BOTTLE
        );

        createProduct(
                ProductType.LUBRICANTS,
                UnitOfMeasure.BOTTLE
        );

        createProduct(
                ProductType.COOLANT,
                UnitOfMeasure.BOTTLE
        );

        createProduct(
                ProductType.TYRE,
                UnitOfMeasure.PACK
        );

        createProduct(
                ProductType.WATER,
                UnitOfMeasure.BOTTLE
        );
    }

    private void createProduct(
            ProductType productType,
            UnitOfMeasure unitOfMeasure) {

        if (productRepository.existsByProductType(productType)) {
            return;
        }

        Product product = Product.builder()
                .name(productType.name())
                .productType(productType)
                .unitOfMeasure(unitOfMeasure)
                .active(true)
                .description(productType.getDescription())
                .build();

        productRepository.save(product);
    }
}
