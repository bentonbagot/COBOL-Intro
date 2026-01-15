package com.cobolmodernization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cobolmodernization.entity.Product;
import com.cobolmodernization.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable String code) {
        return productService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{code}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable String code,
            @RequestParam int quantity) {
        Product updatedProduct = productService.updateStock(code, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String code) {
        productService.deleteProduct(code);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/initialize")
    public ResponseEntity<List<Product>> initializeProducts(@RequestBody List<Product> products) {
        List<Product> initializedProducts = productService.initializeProducts(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(initializedProducts);
    }

    @PostMapping("/initialize-defaults")
    public ResponseEntity<List<Product>> initializeDefaultProducts() {
        List<Product> initializedProducts = productService.initializeDefaultProducts();
        return ResponseEntity.status(HttpStatus.CREATED).body(initializedProducts);
    }
}
