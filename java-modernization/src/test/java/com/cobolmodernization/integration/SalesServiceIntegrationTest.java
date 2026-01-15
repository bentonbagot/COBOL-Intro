package com.cobolmodernization.integration;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.ProductService;
import com.cobolmodernization.service.SalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SalesService.
 * Validates that Java implementation produces identical results to COBOL:
 * - excercise3-sells.cbl / CREA-VENTAS.cbl
 * - ACTUALIZA-PRODUCTOS.cbl / UPDATE-PRODUCTS.cbl
 * 
 * Tests the execution order dependency:
 * CREATE-PRODUCTS -> CREATE-SELLS -> UPDATE-PRODUCTS
 */
@SpringBootTest
@Transactional
class SalesServiceIntegrationTest {

    @Autowired
    private SalesService salesService;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService.initializeProducts();
    }

    @Test
    @DisplayName("Create sale with product validation - equivalent to COBOL ESCRIBIR-VENTA")
    void testCreateSaleWithValidProduct() {
        SaleDto dto = SaleDto.builder()
                .productCode("00001")
                .quantity(5)
                .build();

        SaleDto created = salesService.createSale(dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("00001", created.getProductCode());
        assertEquals(5, created.getQuantity());
        assertFalse(created.getProcessed());
    }

    @Test
    @DisplayName("Sale validation - product must exist (COBOL INVALID KEY)")
    void testCreateSaleWithInvalidProduct() {
        SaleDto dto = SaleDto.builder()
                .productCode("99999")
                .quantity(5)
                .build();

        assertThrows(RecordNotFoundException.class, () -> salesService.createSale(dto));
    }

    @Test
    @DisplayName("Generate predefined sales - equivalent to COBOL GENERAR-VENTAS")
    void testGeneratePredefinedSales() {
        BatchProcessingResult result = salesService.generatePredefinedSales();

        assertEquals(5, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(5, result.getTotalProcessed());

        List<SaleDto> sales = salesService.findAll();
        assertEquals(5, sales.size());
    }

    @Test
    @DisplayName("Update inventory from sales - equivalent to COBOL ACTUALIZA-PRODUCTOS")
    void testUpdateInventoryFromSales() {
        salesService.generatePredefinedSales();

        ProductDto productBefore = productService.findByCode("00001");
        int stockBefore = productBefore.getStock();

        BatchProcessingResult result = salesService.updateInventoryFromSales();

        assertEquals(5, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());

        ProductDto productAfter = productService.findByCode("00001");
        assertEquals(stockBefore - 7, productAfter.getStock());
    }

    @Test
    @DisplayName("Full execution flow - CREATE-PRODUCTS -> CREATE-SELLS -> UPDATE-PRODUCTS")
    void testFullExecutionFlow() {
        ProductDto product1Before = productService.findByCode("00001");
        ProductDto product2Before = productService.findByCode("00002");
        ProductDto product3Before = productService.findByCode("00003");

        BatchProcessingResult salesResult = salesService.generatePredefinedSales();
        assertEquals(5, salesResult.getSuccessCount());

        BatchProcessingResult updateResult = salesService.updateInventoryFromSales();
        assertEquals(5, updateResult.getSuccessCount());

        ProductDto product1After = productService.findByCode("00001");
        ProductDto product2After = productService.findByCode("00002");
        ProductDto product3After = productService.findByCode("00003");

        assertEquals(product1Before.getStock() - 7, product1After.getStock());
        assertEquals(product2Before.getStock() - 11, product2After.getStock());
        assertEquals(product3Before.getStock() - 3, product3After.getStock());
    }

    @Test
    @DisplayName("Sales are marked as processed after inventory update")
    void testSalesMarkedAsProcessed() {
        salesService.generatePredefinedSales();
        assertEquals(5, salesService.countUnprocessedSales());

        salesService.updateInventoryFromSales();
        assertEquals(0, salesService.countUnprocessedSales());
    }

    @Test
    @DisplayName("Update inventory with streams - modern Java approach")
    void testUpdateInventoryWithStreams() {
        salesService.generatePredefinedSales();

        BatchProcessingResult result = salesService.updateInventoryWithStreams();

        assertEquals(5, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, salesService.countUnprocessedSales());
    }

    @Test
    @DisplayName("Sales data matches COBOL VENTAS-RECORD structure")
    void testSalesDataStructure() {
        SaleDto dto = SaleDto.builder()
                .productCode("00001")
                .quantity(99999)
                .build();

        SaleDto created = salesService.createSale(dto);

        assertEquals(5, created.getProductCode().length());
        assertTrue(created.getQuantity() <= 99999);
    }
}
