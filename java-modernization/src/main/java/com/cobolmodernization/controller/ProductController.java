package com.cobolmodernization.controller;

import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product operations.
 * Transforms COBOL interactive product entry into REST API endpoints.
 * 
 * COBOL Program Mapping:
 * - createdat.cbl / CREATE-PRODUCTS.cbl -> POST /api/products
 * - READ operations -> GET /api/products/{code}
 * - REWRITE operations -> PUT /api/products/{code}
 * - DELETE operations -> DELETE /api/products/{code}
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product.
     * Translated from COBOL WRITE PRODUCTS-RECORD.
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
        ProductDto created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get product by code.
     * Translated from COBOL READ PRODUCTS-FILE KEY IS PROD-CODE.
     */
    @GetMapping("/{code}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String code) {
        ProductDto product = productService.findByCode(code);
        return ResponseEntity.ok(product);
    }

    /**
     * Get all products.
     * Translated from COBOL sequential read of indexed file.
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Update an existing product.
     * Translated from COBOL REWRITE PRODUCTS-RECORD.
     */
    @PutMapping("/{code}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String code,
            @Valid @RequestBody ProductDto dto) {
        ProductDto updated = productService.updateProduct(code, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a product.
     * Translated from COBOL DELETE PRODUCTS-FILE.
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String code) {
        productService.deleteProduct(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * Initialize products with predefined data.
     * Translated from COBOL CREATE-PRODUCTS.cbl predefined entries.
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeProducts() {
        productService.initializeProducts();
        return ResponseEntity.ok("Products initialized successfully");
    }

    /**
     * Check if product exists.
     */
    @GetMapping("/{code}/exists")
    public ResponseEntity<Boolean> productExists(@PathVariable String code) {
        boolean exists = productService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}
