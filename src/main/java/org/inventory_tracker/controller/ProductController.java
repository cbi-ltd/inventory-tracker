package org.inventory_tracker.controller;

import org.inventory_tracker.dto.request.CreateProductRequest;
import org.inventory_tracker.dto.request.UpdateProductRequest;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.ProductResponse;
import org.inventory_tracker.enums.ProductType;
import org.inventory_tracker.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse<>(
                                LocalDateTime.now(),
                                HttpStatus.CREATED.value(),
                                "Product created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> response =
                productService.getAllProducts();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Products retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {

        ProductResponse response =
                productService.getProductById(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product retrieved successfully",
                        response
                )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiSuccessResponse<List<ProductResponse>>> getActiveProducts() {

        List<ProductResponse> response =
                productService.getActiveProducts();

        int count = response.size();

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Active products retrieved successfully",
                        count,
                        response
                )
        );
    }

    @GetMapping("/type/{productType}")
    public ResponseEntity<ApiSuccessResponse<List<ProductResponse>>> getProductsByType(
            @PathVariable String productType) {

        ProductType type = ProductType.fromValue(productType);
        List<ProductResponse> response =
                productService.getProductsByType(type);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Products retrieved successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiSuccessResponse<ProductResponse>> activateProduct(
            @PathVariable Long id) {

        ProductResponse response =
                productService.activateProduct(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiSuccessResponse<ProductResponse>> deactivateProduct(
            @PathVariable Long id) {

        ProductResponse response =
                productService.deactivateProduct(id);

        return ResponseEntity.ok(
                new ApiSuccessResponse<>(
                        LocalDateTime.now(),
                        HttpStatus.OK.value(),
                        "Product deactivated successfully",
                        response
                )
        );
    }
}
