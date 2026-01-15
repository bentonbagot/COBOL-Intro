package com.cobolmodernization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cobolmodernization.entity.SalesRecordEntity;
import com.cobolmodernization.model.SalesRecord;
import com.cobolmodernization.service.SalesService;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping
    public ResponseEntity<SalesRecordEntity> createSale(@RequestBody SalesRecord sale) {
        SalesRecordEntity createdSale = salesService.createSale(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SalesRecordEntity>> processBatchSales(@RequestBody List<SalesRecord> sales) {
        List<SalesRecordEntity> processedSales = salesService.processSalesTransactions(sales);
        return ResponseEntity.status(HttpStatus.CREATED).body(processedSales);
    }

    @PostMapping("/update-inventory")
    public ResponseEntity<Void> updateInventoryFromSales(@RequestBody List<SalesRecord> sales) {
        salesService.updateInventoryFromSales(sales);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process-and-update")
    public ResponseEntity<Void> processAndUpdateInventory(@RequestBody List<SalesRecord> sales) {
        salesService.processAndUpdateInventory(sales);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SalesRecordEntity>> getAllSales() {
        List<SalesRecordEntity> sales = salesService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/product/{productCode}")
    public ResponseEntity<List<SalesRecordEntity>> getSalesByProduct(@PathVariable String productCode) {
        List<SalesRecordEntity> sales = salesService.getSalesByProductCode(productCode);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/report")
    public ResponseEntity<List<SalesRecord>> generateSalesReport() {
        List<SalesRecord> report = salesService.generateSalesReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/generate-defaults")
    public ResponseEntity<List<SalesRecordEntity>> generateDefaultSales() {
        List<SalesRecordEntity> sales = salesService.generateDefaultSales();
        return ResponseEntity.status(HttpStatus.CREATED).body(sales);
    }
}
