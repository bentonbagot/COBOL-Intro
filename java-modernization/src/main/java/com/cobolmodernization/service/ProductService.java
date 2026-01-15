package com.cobolmodernization.service;

import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.entity.Product;
import com.cobolmodernization.exception.DuplicateKeyException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Product operations.
 * Translates business logic from COBOL programs:
 * - createdat.cbl / CREATE-PRODUCTS.cbl - Product initialization with duplicate detection
 * - NUEVOS-PRODUCTOS.cbl / NEW-PRODUCTS.cbl - Interactive product addition
 * 
 * COBOL File Status Mapping:
 * - 00 (success) -> normal return
 * - 22 (duplicate) -> DuplicateKeyException
 * - 23 (not found) -> RecordNotFoundException
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product.
     * Translated from COBOL WRITE PRODUCTS-RECORD with INVALID KEY handling.
     * 
     * @param dto the product data
     * @return the created product
     * @throws DuplicateKeyException if product code already exists (COBOL status 22)
     */
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        log.info("Creating product with code: {}", dto.getCode());
        
        if (productRepository.existsByCode(dto.getCode())) {
            log.warn("Duplicate product code: {}", dto.getCode());
            throw new DuplicateKeyException("Product", dto.getCode());
        }

        Product product = Product.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product successfully registered: {}", saved.getCode());
        
        return toDto(saved);
    }

    /**
     * Find product by code.
     * Translated from COBOL READ PRODUCTS-FILE KEY IS PROD-CODE.
     * 
     * @param code the product code
     * @return the product
     * @throws RecordNotFoundException if product not found (COBOL status 23)
     */
    @Transactional(readOnly = true)
    public ProductDto findByCode(String code) {
        log.info("Searching for product with code: {}", code);
        
        return productRepository.findByCode(code)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", code);
                    return new RecordNotFoundException("Product", code);
                });
    }

    /**
     * Get all products.
     * Translated from COBOL sequential read of indexed file.
     * 
     * @return list of all products
     */
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        log.info("Retrieving all products");
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing product.
     * Translated from COBOL REWRITE PRODUCTS-RECORD.
     * 
     * @param code the product code
     * @param dto the updated product data
     * @return the updated product
     * @throws RecordNotFoundException if product not found
     */
    @Transactional
    public ProductDto updateProduct(String code, ProductDto dto) {
        log.info("Updating product with code: {}", code);
        
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new RecordNotFoundException("Product", code));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        Product saved = productRepository.save(product);
        log.info("Product updated: {}", saved.getCode());
        
        return toDto(saved);
    }

    /**
     * Delete a product.
     * Translated from COBOL DELETE PRODUCTS-FILE.
     * 
     * @param code the product code
     * @throws RecordNotFoundException if product not found
     */
    @Transactional
    public void deleteProduct(String code) {
        log.info("Deleting product with code: {}", code);
        
        if (!productRepository.existsByCode(code)) {
            throw new RecordNotFoundException("Product", code);
        }
        
        productRepository.deleteById(code);
        log.info("Product deleted: {}", code);
    }

    /**
     * Check if product exists.
     * Used for validation before creating sales.
     * 
     * @param code the product code
     * @return true if product exists
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return productRepository.existsByCode(code);
    }

    /**
     * Get product entity by code (for internal use).
     * 
     * @param code the product code
     * @return the product entity
     * @throws RecordNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public Product getProductEntity(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new RecordNotFoundException("Product", code));
    }

    /**
     * Initialize products with predefined data.
     * Translated from COBOL CREATE-PRODUCTS.cbl predefined product entries.
     */
    @Transactional
    public void initializeProducts() {
        log.info("Initializing products with predefined data");
        
        createProductIfNotExists("00001", "Product A", new java.math.BigDecimal("100.00"), 50);
        createProductIfNotExists("00002", "Product B", new java.math.BigDecimal("200.00"), 30);
        createProductIfNotExists("00003", "Product C", new java.math.BigDecimal("150.00"), 25);
        
        log.info("Product initialization complete");
    }

    private void createProductIfNotExists(String code, String name, java.math.BigDecimal price, int stock) {
        if (!productRepository.existsByCode(code)) {
            ProductDto dto = ProductDto.builder()
                    .code(code)
                    .name(name)
                    .price(price)
                    .stock(stock)
                    .build();
            createProduct(dto);
        }
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
