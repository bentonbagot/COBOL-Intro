package com.cobolintro.service;

import com.cobolintro.dao.ProductDao;
import com.cobolintro.model.Product;

import java.math.BigDecimal;

/**
 * Service layer for product operations.
 * Replaces PROCESS paragraph in createdat.cbl and product initialization in excercise2.cbl.
 */
public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Validates and creates a new product record.
     *
     * @param p the product to create
     * @throws IllegalArgumentException if code or name is null/empty
     */
    public void createProduct(Product p) {
        if (p.getCode() == null || p.getCode().isEmpty()) {
            throw new IllegalArgumentException("Product code must not be null or empty");
        }
        if (p.getName() == null || p.getName().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be null or empty");
        }
        productDao.create(p);
    }

    /**
     * Batch loads hardcoded sample products into the database.
     * Replaces the logic in excercise2.cbl that pre-populates the products file.
     */
    public void initializeProducts() {
        productDao.create(new Product("P0001", "Laptop", new BigDecimal("999.99"), 50));
        productDao.create(new Product("P0002", "Wireless Mouse", new BigDecimal("29.95"), 200));
        productDao.create(new Product("P0003", "USB-C Hub", new BigDecimal("49.99"), 150));
        productDao.create(new Product("P0004", "Granola Bars (12pk)", new BigDecimal("8.49"), 500));
        productDao.create(new Product("P0005", "Coffee Beans 1kg", new BigDecimal("14.99"), 300));
        productDao.create(new Product("P0006", "Mechanical Keyboard", new BigDecimal("74.50"), 120));
        productDao.create(new Product("P0007", "HDMI Cable 2m", new BigDecimal("12.99"), 400));
    }

    /**
     * Looks up a product by its code.
     *
     * @param code the product code to search for
     * @return the matching {@link Product}
     * @throws com.cobolintro.exception.RecordNotFoundException if no product exists for the code
     */
    public Product lookupProduct(String code) {
        return productDao.findByCode(code);
    }
}
