package com.cobolmodernization.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cobolmodernization.entity.Product;
import com.cobolmodernization.exception.DuplicateProductException;
import com.cobolmodernization.exception.InsufficientStockException;
import com.cobolmodernization.exception.ProductNotFoundException;
import com.cobolmodernization.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        logger.info("Creating product with code: {}", product.getCode());

        if (productRepository.existsByCode(product.getCode())) {
            logger.error("Duplicate product code detected: {}", product.getCode());
            throw new DuplicateProductException(product.getCode());
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Product successfully registered: {}", savedProduct.getCode());
        return savedProduct;
    }

    @Transactional(readOnly = true)
    public Optional<Product> findByCode(String code) {
        logger.debug("Searching for product with code: {}", code);
        return productRepository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Product getByCode(String code) {
        return findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(code));
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        logger.debug("Retrieving all products");
        return productRepository.findAll();
    }

    public Product updateStock(String code, int quantityChange) {
        logger.info("Updating stock for product {}: change of {}", code, quantityChange);

        Product product = getByCode(code);
        int newStock = product.getStock() + quantityChange;

        if (newStock < 0) {
            logger.error("Insufficient stock for product {}: requested {}, available {}",
                    code, Math.abs(quantityChange), product.getStock());
            throw new InsufficientStockException(code, Math.abs(quantityChange), product.getStock());
        }

        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        logger.info("Stock updated for product {}: new stock = {}", code, newStock);
        return updatedProduct;
    }

    public void decreaseStock(String code, int quantity) {
        updateStock(code, -quantity);
    }

    public void increaseStock(String code, int quantity) {
        updateStock(code, quantity);
    }

    public List<Product> initializeProducts(List<Product> products) {
        logger.info("Initializing {} products", products.size());
        List<Product> savedProducts = new ArrayList<>();

        for (Product product : products) {
            try {
                Product saved = createProduct(product);
                savedProducts.add(saved);
            } catch (DuplicateProductException e) {
                logger.warn("Skipping duplicate product: {}", product.getCode());
            }
        }

        logger.info("Successfully initialized {} products", savedProducts.size());
        return savedProducts;
    }

    public List<Product> initializeDefaultProducts() {
        logger.info("Initializing default product catalog");

        List<Product> defaultProducts = new ArrayList<>();
        defaultProducts.add(new Product("00001", "LAPTOP DELL", new BigDecimal("1500.00"), 100));
        defaultProducts.add(new Product("00002", "MOUSE LOGITECH", new BigDecimal("25.99"), 500));
        defaultProducts.add(new Product("00003", "KEYBOARD MECH", new BigDecimal("89.99"), 200));
        defaultProducts.add(new Product("00004", "MONITOR 27IN", new BigDecimal("350.00"), 75));
        defaultProducts.add(new Product("00005", "USB HUB 4PORT", new BigDecimal("19.99"), 300));

        return initializeProducts(defaultProducts);
    }

    public boolean existsByCode(String code) {
        return productRepository.existsByCode(code);
    }

    public void deleteProduct(String code) {
        logger.info("Deleting product with code: {}", code);
        Product product = getByCode(code);
        productRepository.delete(product);
        logger.info("Product deleted: {}", code);
    }

    public long countProducts() {
        return productRepository.count();
    }
}
