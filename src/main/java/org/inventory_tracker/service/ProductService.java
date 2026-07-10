package org.inventory_tracker.service;

import org.inventory_tracker.dto.request.CreateProductRequest;
import org.inventory_tracker.dto.request.UpdateProductRequest;
import org.inventory_tracker.dto.response.ProductResponse;
import org.inventory_tracker.exception.ResourceNotFoundException;
import org.inventory_tracker.config.mapper.ProductMapper;
import org.inventory_tracker.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.inventory_tracker.entity.Product;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.exception.DuplicateResourceException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        productRepository.findByNameIgnoreCase(request.getName())
        .ifPresent(product -> {
            throw new DuplicateResourceException(
                    "Product already exists");
        });

        // if (productRepository.existsByNameIgnoreCase(request.getName())) {
        //     throw new DuplicateResourceException(
        //             "Product already exists");
        // }

        Product product =
                productMapper.toEntity(request);

        Product saved =
                productRepository.save(product);

        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long id,
            UpdateProductRequest request) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"));

        if (!product.getName().equalsIgnoreCase(request.getName())
                && productRepository.existsByNameIgnoreCase(request.getName())) {

            throw new DuplicateResourceException(
                    "Product already exists");
        }

        // if (!product.getName().equalsIgnoreCase(request.getName())
        // && productRepository.existsByNameIgnoreCase(request.getName())) {

        //     throw new DuplicateResourceException(
        //             "Product already exists");
        // }

        product.setName(request.getName());
        product.setProductType(request.getProductType());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setDescription(request.getDescription());

        Product updated =
                productRepository.save(product);

        return productMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"));

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productMapper.toResponseList(
                productRepository.findAllByOrderByNameAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {

        return productMapper.toResponseList(
                productRepository.findByActiveTrueOrderByNameAsc()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByType(
            ProductType productType) {

        return productMapper.toResponseList(
                productRepository.findByProductTypeOrderByNameAsc(
                        productType
                )
        );
    }

    @Transactional
    public ProductResponse activateProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"));

        if (Boolean.TRUE.equals(product.getActive())) {
            throw new DuplicateResourceException(
                    "Product is already active");
        }

        product.setActive(true);

        Product updated =
                productRepository.save(product);

        return productMapper.toResponse(updated);
    }

    @Transactional
    public ProductResponse deactivateProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found"));

        if (Boolean.FALSE.equals(product.getActive())) {
            throw new DuplicateResourceException(
                    "Product is already inactive");
        }

        product.setActive(false);

        Product updated =
                productRepository.save(product);

        return productMapper.toResponse(updated);
    }
}
