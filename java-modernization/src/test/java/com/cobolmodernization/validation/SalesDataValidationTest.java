package com.cobolmodernization.validation;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.dto.ProductDto;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.service.ProductService;
import com.cobolmodernization.service.SalesService;
import com.cobolmodernization.validation.DataComparisonUtils.ComparisonResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for Sales data consistency.
 * Validates that Java implementation produces identical results to COBOL programs:
 * - CREATE-SELLS.cob / excercise3-sells.cbl - Sales transaction generation
 * - UPDATE-PRODUCTS.cob - Batch inventory updates from sales data
 * 
 * Tests verify:
 * 1. Sales data structure matches COBOL VENTAS-RECORD (PIC clauses)
 * 2. Sales generation matches COBOL GENERAR-VENTAS procedure
 * 3. Inventory update matches COBOL ACTUALIZA-PRODUCTOS logic
 * 4. Execution order dependency: CREATE-PRODUCTS -> CREATE-SELLS -> UPDATE-PRODUCTS
 */
@SpringBootTest
@Transactional
class SalesDataValidationTest {

    @Autowired
    private SalesService salesService;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService.initializeProducts();
    }

    @Nested
    @DisplayName("CREATE-SELLS.cob Validation - Sales Transaction Generation")
    class CreateSellsValidation {

        @Test
        @DisplayName("Generate predefined sales matches COBOL GENERAR-VENTAS")
        void validateGeneratePredefinedSales() {
            BatchProcessingResult result = salesService.generatePredefinedSales();

            assertEquals(5, result.getSuccessCount(), 
                    "Should generate 5 sales as in COBOL GENERAR-VENTAS");
            assertEquals(0, result.getFailureCount(), 
                    "Should have no failures when products exist");
        }

        @Test
        @DisplayName("Predefined sales match COBOL GENERAR-VENTAS data")
        void validatePredefinedSalesData() {
            salesService.generatePredefinedSales();
            
            List<SaleDto> sales = salesService.findAll();
            ComparisonResult result = DataComparisonUtils.validatePredefinedSales(sales);

            assertTrue(result.isMatch(), 
                    "Predefined sales should match COBOL GENERAR-VENTAS data: " + result);
        }

        @Test
        @DisplayName("Sale 1: Product 00001, Quantity 5")
        void validateSale1() {
            salesService.generatePredefinedSales();
            List<SaleDto> sales = salesService.findAll();

            assertTrue(sales.size() >= 1);
            SaleDto sale = sales.get(0);
            assertEquals("00001", sale.getProductCode());
            assertEquals(5, sale.getQuantity());
            assertFalse(sale.getProcessed());
        }

        @Test
        @DisplayName("Sale 2: Product 00002, Quantity 10")
        void validateSale2() {
            salesService.generatePredefinedSales();
            List<SaleDto> sales = salesService.findAll();

            assertTrue(sales.size() >= 2);
            SaleDto sale = sales.get(1);
            assertEquals("00002", sale.getProductCode());
            assertEquals(10, sale.getQuantity());
        }

        @Test
        @DisplayName("Sale 3: Product 00003, Quantity 3")
        void validateSale3() {
            salesService.generatePredefinedSales();
            List<SaleDto> sales = salesService.findAll();

            assertTrue(sales.size() >= 3);
            SaleDto sale = sales.get(2);
            assertEquals("00003", sale.getProductCode());
            assertEquals(3, sale.getQuantity());
        }

        @Test
        @DisplayName("Sale 4: Product 00001, Quantity 2 (second sale for same product)")
        void validateSale4() {
            salesService.generatePredefinedSales();
            List<SaleDto> sales = salesService.findAll();

            assertTrue(sales.size() >= 4);
            SaleDto sale = sales.get(3);
            assertEquals("00001", sale.getProductCode());
            assertEquals(2, sale.getQuantity());
        }

        @Test
        @DisplayName("Sale 5: Product 00002, Quantity 1")
        void validateSale5() {
            salesService.generatePredefinedSales();
            List<SaleDto> sales = salesService.findAll();

            assertTrue(sales.size() >= 5);
            SaleDto sale = sales.get(4);
            assertEquals("00002", sale.getProductCode());
            assertEquals(1, sale.getQuantity());
        }

        @Test
        @DisplayName("Product validation matches COBOL READ ... INVALID KEY")
        void validateProductValidation() {
            SaleDto dto = SaleDto.builder()
                    .productCode("NOTEX")
                    .quantity(5)
                    .build();

            assertThrows(RecordNotFoundException.class,
                    () -> salesService.createSale(dto));
        }

        @Test
        @DisplayName("Create sale with valid product matches COBOL ESCRIBIR-VENTA")
        void validateCreateSaleWithValidProduct() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(10)
                    .build();

            SaleDto created = salesService.createSale(dto);

            assertNotNull(created);
            assertNotNull(created.getId());
            assertEquals("00001", created.getProductCode());
            assertEquals(10, created.getQuantity());
            assertFalse(created.getProcessed());
        }
    }

    @Nested
    @DisplayName("UPDATE-PRODUCTS.cob Validation - Batch Inventory Updates")
    class UpdateProductsValidation {

        @Test
        @DisplayName("Inventory update matches COBOL ACTUALIZA-PRODUCTOS")
        void validateInventoryUpdate() {
            List<ProductDto> productsBefore = new ArrayList<>();
            for (ProductDto p : productService.findAll()) {
                productsBefore.add(ProductDto.builder()
                        .code(p.getCode())
                        .name(p.getName())
                        .price(p.getPrice())
                        .stock(p.getStock())
                        .build());
            }

            salesService.generatePredefinedSales();
            BatchProcessingResult result = salesService.updateInventoryFromSales();

            assertEquals(5, result.getSuccessCount(), 
                    "Should process 5 sales");
            assertEquals(0, result.getFailureCount(), 
                    "Should have no failures");

            List<ProductDto> productsAfter = productService.findAll();
            List<SaleDto> sales = salesService.findAll();

            ComparisonResult comparison = DataComparisonUtils.validateInventoryUpdate(
                    productsBefore, productsAfter, sales);

            assertTrue(comparison.isMatch(), 
                    "Inventory update should match COBOL logic: " + comparison);
        }

        @Test
        @DisplayName("Product 00001 stock reduced by 7 (5 + 2)")
        void validateProduct00001StockUpdate() {
            ProductDto before = productService.findByCode("00001");
            int stockBefore = before.getStock();

            salesService.generatePredefinedSales();
            salesService.updateInventoryFromSales();

            ProductDto after = productService.findByCode("00001");
            assertEquals(stockBefore - 7, after.getStock(),
                    "Product 00001 should have stock reduced by 7 (sales: 5 + 2)");
        }

        @Test
        @DisplayName("Product 00002 stock reduced by 11 (10 + 1)")
        void validateProduct00002StockUpdate() {
            ProductDto before = productService.findByCode("00002");
            int stockBefore = before.getStock();

            salesService.generatePredefinedSales();
            salesService.updateInventoryFromSales();

            ProductDto after = productService.findByCode("00002");
            assertEquals(stockBefore - 11, after.getStock(),
                    "Product 00002 should have stock reduced by 11 (sales: 10 + 1)");
        }

        @Test
        @DisplayName("Product 00003 stock reduced by 3")
        void validateProduct00003StockUpdate() {
            ProductDto before = productService.findByCode("00003");
            int stockBefore = before.getStock();

            salesService.generatePredefinedSales();
            salesService.updateInventoryFromSales();

            ProductDto after = productService.findByCode("00003");
            assertEquals(stockBefore - 3, after.getStock(),
                    "Product 00003 should have stock reduced by 3");
        }

        @Test
        @DisplayName("Sales marked as processed after inventory update")
        void validateSalesMarkedAsProcessed() {
            salesService.generatePredefinedSales();
            assertEquals(5, salesService.countUnprocessedSales());

            salesService.updateInventoryFromSales();

            assertEquals(0, salesService.countUnprocessedSales(),
                    "All sales should be marked as processed");
        }

        @Test
        @DisplayName("Stream-based inventory update produces same results")
        void validateStreamBasedInventoryUpdate() {
            List<ProductDto> productsBefore = new ArrayList<>();
            for (ProductDto p : productService.findAll()) {
                productsBefore.add(ProductDto.builder()
                        .code(p.getCode())
                        .name(p.getName())
                        .price(p.getPrice())
                        .stock(p.getStock())
                        .build());
            }

            salesService.generatePredefinedSales();
            BatchProcessingResult result = salesService.updateInventoryWithStreams();

            assertEquals(5, result.getSuccessCount());
            assertEquals(0, result.getFailureCount());

            ProductDto product1 = productService.findByCode("00001");
            ProductDto product2 = productService.findByCode("00002");
            ProductDto product3 = productService.findByCode("00003");

            assertEquals(productsBefore.get(0).getStock() - 7, product1.getStock());
            assertEquals(productsBefore.get(1).getStock() - 11, product2.getStock());
            assertEquals(productsBefore.get(2).getStock() - 3, product3.getStock());
        }
    }

    @Nested
    @DisplayName("Execution Order Validation - CREATE-PRODUCTS -> CREATE-SELLS -> UPDATE-PRODUCTS")
    class ExecutionOrderValidation {

        @Test
        @DisplayName("Full execution flow matches COBOL program sequence")
        void validateFullExecutionFlow() {
            ProductDto p1Before = productService.findByCode("00001");
            ProductDto p2Before = productService.findByCode("00002");
            ProductDto p3Before = productService.findByCode("00003");

            BatchProcessingResult salesResult = salesService.generatePredefinedSales();
            assertEquals(5, salesResult.getSuccessCount());

            BatchProcessingResult updateResult = salesService.updateInventoryFromSales();
            assertEquals(5, updateResult.getSuccessCount());

            ProductDto p1After = productService.findByCode("00001");
            ProductDto p2After = productService.findByCode("00002");
            ProductDto p3After = productService.findByCode("00003");

            assertEquals(p1Before.getStock() - 7, p1After.getStock());
            assertEquals(p2Before.getStock() - 11, p2After.getStock());
            assertEquals(p3Before.getStock() - 3, p3After.getStock());
        }

        @Test
        @DisplayName("Sales without products fails - validates execution order dependency")
        void validateExecutionOrderDependency() {
            productService.deleteProduct("00001");
            productService.deleteProduct("00002");
            productService.deleteProduct("00003");

            BatchProcessingResult result = salesService.generatePredefinedSales();

            assertEquals(0, result.getSuccessCount(),
                    "Should fail to create sales without products");
            assertEquals(5, result.getFailureCount(),
                    "All 5 sales should fail");
        }

        @Test
        @DisplayName("Partial product availability - some sales succeed")
        void validatePartialProductAvailability() {
            productService.deleteProduct("00003");

            BatchProcessingResult result = salesService.generatePredefinedSales();

            assertEquals(4, result.getSuccessCount(),
                    "4 sales should succeed (products 00001 and 00002 exist)");
            assertEquals(1, result.getFailureCount(),
                    "1 sale should fail (product 00003 missing)");
        }
    }

    @Nested
    @DisplayName("COBOL Data Structure Validation - VENTAS-RECORD PIC Clauses")
    class DataStructureValidation {

        @Test
        @DisplayName("Sale product code matches COBOL PIC X(05)")
        void validateSaleProductCodeLength() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(1)
                    .build();

            SaleDto created = salesService.createSale(dto);
            assertEquals(5, created.getProductCode().length());
        }

        @Test
        @DisplayName("Sale quantity matches COBOL PIC 9(05) - max value")
        void validateSaleQuantityMaxValue() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(99999)
                    .build();

            SaleDto created = salesService.createSale(dto);
            assertEquals(99999, created.getQuantity());
        }

        @Test
        @DisplayName("Sale has timestamp - enhanced from COBOL")
        void validateSaleTimestamp() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(1)
                    .build();

            SaleDto created = salesService.createSale(dto);
            assertNotNull(created.getCreatedAt(),
                    "Sale should have timestamp (enhanced from COBOL)");
        }

        @Test
        @DisplayName("Sale has processed flag - enhanced from COBOL")
        void validateSaleProcessedFlag() {
            SaleDto dto = SaleDto.builder()
                    .productCode("00001")
                    .quantity(1)
                    .build();

            SaleDto created = salesService.createSale(dto);
            assertNotNull(created.getProcessed());
            assertFalse(created.getProcessed(),
                    "New sale should not be processed");
        }
    }

    @Nested
    @DisplayName("Sales Query Validation")
    class SalesQueryValidation {

        @Test
        @DisplayName("Find sales by product code")
        void validateFindByProductCode() {
            salesService.generatePredefinedSales();

            List<SaleDto> product1Sales = salesService.findByProductCode("00001");
            assertEquals(2, product1Sales.size(),
                    "Product 00001 should have 2 sales");

            List<SaleDto> product2Sales = salesService.findByProductCode("00002");
            assertEquals(2, product2Sales.size(),
                    "Product 00002 should have 2 sales");

            List<SaleDto> product3Sales = salesService.findByProductCode("00003");
            assertEquals(1, product3Sales.size(),
                    "Product 00003 should have 1 sale");
        }

        @Test
        @DisplayName("Find all sales returns correct count")
        void validateFindAllSales() {
            salesService.generatePredefinedSales();

            List<SaleDto> allSales = salesService.findAll();
            assertEquals(5, allSales.size());
        }

        @Test
        @DisplayName("Count unprocessed sales")
        void validateCountUnprocessedSales() {
            assertEquals(0, salesService.countUnprocessedSales());

            salesService.generatePredefinedSales();
            assertEquals(5, salesService.countUnprocessedSales());

            salesService.updateInventoryFromSales();
            assertEquals(0, salesService.countUnprocessedSales());
        }
    }
}
