package com.cobolmodernization.controller;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.service.SalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sales operations.
 * Transforms COBOL sales processing into REST API endpoints.
 * 
 * COBOL Program Mapping:
 * - excercise3-sells.cbl / CREA-VENTAS.cbl -> POST /api/sales, POST /api/sales/generate
 * - ACTUALIZA-PRODUCTOS.cbl / UPDATE-PRODUCTS.cbl -> POST /api/sales/update-inventory
 * 
 * Execution Order Dependency:
 * 1. Initialize products (POST /api/products/initialize)
 * 2. Generate sales (POST /api/sales/generate)
 * 3. Update inventory (POST /api/sales/update-inventory)
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    /**
     * Create a new sale.
     * Translated from COBOL ESCRIBIR-VENTA procedure.
     * Validates product exists before creating sale.
     */
    @PostMapping
    public ResponseEntity<SaleDto> createSale(@Valid @RequestBody SaleDto dto) {
        SaleDto created = salesService.createSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all sales.
     */
    @GetMapping
    public ResponseEntity<List<SaleDto>> getAllSales() {
        List<SaleDto> sales = salesService.findAll();
        return ResponseEntity.ok(sales);
    }

    /**
     * Get sales by product code.
     */
    @GetMapping("/product/{productCode}")
    public ResponseEntity<List<SaleDto>> getSalesByProduct(@PathVariable String productCode) {
        List<SaleDto> sales = salesService.findByProductCode(productCode);
        return ResponseEntity.ok(sales);
    }

    /**
     * Generate predefined sales.
     * Translated from COBOL GENERAR-VENTAS procedure.
     * Creates the same sales as the original COBOL program.
     */
    @PostMapping("/generate")
    public ResponseEntity<BatchProcessingResult> generatePredefinedSales() {
        BatchProcessingResult result = salesService.generatePredefinedSales();
        return ResponseEntity.ok(result);
    }

    /**
     * Update inventory from sales.
     * Translated from COBOL ACTUALIZA-PRODUCTOS / UPDATE-PRODUCTS.
     * Processes all unprocessed sales and updates product stock.
     */
    @PostMapping("/update-inventory")
    public ResponseEntity<BatchProcessingResult> updateInventory() {
        BatchProcessingResult result = salesService.updateInventoryFromSales();
        return ResponseEntity.ok(result);
    }

    /**
     * Update inventory using Java Streams.
     * Modern streaming approach for batch processing.
     */
    @PostMapping("/update-inventory/stream")
    public ResponseEntity<BatchProcessingResult> updateInventoryWithStreams() {
        BatchProcessingResult result = salesService.updateInventoryWithStreams();
        return ResponseEntity.ok(result);
    }

    /**
     * Get count of unprocessed sales.
     */
    @GetMapping("/unprocessed/count")
    public ResponseEntity<Long> countUnprocessedSales() {
        long count = salesService.countUnprocessedSales();
        return ResponseEntity.ok(count);
    }
}
