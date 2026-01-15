package com.cobolmodernization.service;

import com.cobolmodernization.dto.BatchProcessingResult;
import com.cobolmodernization.dto.SaleDto;
import com.cobolmodernization.entity.Product;
import com.cobolmodernization.entity.Sale;
import com.cobolmodernization.exception.InsufficientStockException;
import com.cobolmodernization.exception.RecordNotFoundException;
import com.cobolmodernization.repository.ProductRepository;
import com.cobolmodernization.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Sales operations.
 * Translates business logic from COBOL programs:
 * - excercise3-sells.cbl / CREA-VENTAS.cbl - Sales transaction generation with product validation
 * - ACTUALIZA-PRODUCTOS.cbl / UPDATE-PRODUCTS.cbl - Batch inventory updates from sales data
 * 
 * Execution Order Dependency (from README):
 * CREATE-PRODUCTS -> CREATE-SELLS -> UPDATE-PRODUCTS
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    /**
     * Create a new sale.
     * Translated from COBOL ESCRIBIR-VENTA procedure.
     * Validates product exists before creating sale (COBOL READ ... INVALID KEY).
     * 
     * @param dto the sale data
     * @return the created sale
     * @throws RecordNotFoundException if product does not exist
     */
    @Transactional
    public SaleDto createSale(SaleDto dto) {
        log.info("Creating sale for product: {}", dto.getProductCode());
        
        if (!productRepository.existsByCode(dto.getProductCode())) {
            log.warn("Product does not exist: {}", dto.getProductCode());
            throw new RecordNotFoundException("Product", dto.getProductCode());
        }

        Sale sale = Sale.builder()
                .productCode(dto.getProductCode())
                .quantity(dto.getQuantity())
                .build();

        Sale saved = saleRepository.save(sale);
        log.info("Sale registered: product={}, quantity={}", saved.getProductCode(), saved.getQuantity());
        
        return toDto(saved);
    }

    /**
     * Get all sales.
     * 
     * @return list of all sales
     */
    @Transactional(readOnly = true)
    public List<SaleDto> findAll() {
        log.info("Retrieving all sales");
        return saleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get sales by product code.
     * 
     * @param productCode the product code
     * @return list of sales for the product
     */
    @Transactional(readOnly = true)
    public List<SaleDto> findByProductCode(String productCode) {
        log.info("Retrieving sales for product: {}", productCode);
        return saleRepository.findByProductCode(productCode).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Generate predefined sales.
     * Translated from COBOL GENERAR-VENTAS procedure in excercise3-sells.cbl.
     * Creates the same sales as the original COBOL program.
     * 
     * @return batch processing result with count of generated sales
     */
    @Transactional
    public BatchProcessingResult generatePredefinedSales() {
        log.info("Generating predefined sales (GENERAR-VENTAS)...");
        BatchProcessingResult result = new BatchProcessingResult();

        createSaleIfProductExists("00001", 5, result);
        createSaleIfProductExists("00002", 10, result);
        createSaleIfProductExists("00003", 3, result);
        createSaleIfProductExists("00001", 2, result);
        createSaleIfProductExists("00002", 1, result);

        log.info("Total sales generated: {}", result.getSuccessCount());
        return result;
    }

    /**
     * Process batch sales and update inventory.
     * Translated from COBOL ACTUALIZA-PRODUCTOS / UPDATE-PRODUCTS logic.
     * Reads all unprocessed sales sequentially and updates product stock.
     * 
     * @return batch processing result
     */
    @Transactional
    public BatchProcessingResult updateInventoryFromSales() {
        log.info("Processing batch inventory update (ACTUALIZA-PRODUCTOS)...");
        BatchProcessingResult result = new BatchProcessingResult();

        List<Sale> unprocessedSales = saleRepository.findByProcessedFalse();
        log.info("Found {} unprocessed sales", unprocessedSales.size());

        for (Sale sale : unprocessedSales) {
            try {
                processInventoryUpdate(sale, result);
            } catch (Exception e) {
                log.error("Error processing sale {}: {}", sale.getId(), e.getMessage());
                result.incrementFailure(String.format("Sale %d: %s", sale.getId(), e.getMessage()));
            }
        }

        log.info("Inventory update complete. Processed: {}, Success: {}, Failures: {}",
                result.getTotalProcessed(), result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    /**
     * Process inventory update using Java Streams.
     * Modern streaming approach for batch processing.
     * 
     * @return batch processing result
     */
    @Transactional
    public BatchProcessingResult updateInventoryWithStreams() {
        log.info("Processing batch inventory update with streams...");
        BatchProcessingResult result = new BatchProcessingResult();

        saleRepository.findByProcessedFalse().stream()
                .forEach(sale -> {
                    try {
                        processInventoryUpdate(sale, result);
                    } catch (Exception e) {
                        log.error("Error processing sale {}: {}", sale.getId(), e.getMessage());
                        result.incrementFailure(String.format("Sale %d: %s", sale.getId(), e.getMessage()));
                    }
                });

        log.info("Stream processing complete. Processed: {}, Success: {}, Failures: {}",
                result.getTotalProcessed(), result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    /**
     * Get count of unprocessed sales.
     * 
     * @return count of unprocessed sales
     */
    @Transactional(readOnly = true)
    public long countUnprocessedSales() {
        return saleRepository.countByProcessedFalse();
    }

    private void createSaleIfProductExists(String productCode, int quantity, BatchProcessingResult result) {
        log.info("Searching code: {}", productCode);
        
        if (productRepository.existsByCode(productCode)) {
            log.info("Product found: {}", productCode);
            Sale sale = Sale.builder()
                    .productCode(productCode)
                    .quantity(quantity)
                    .build();
            saleRepository.save(sale);
            result.incrementSuccess();
            result.addProcessedItem(String.format("Sale registered: %s, qty=%d", productCode, quantity));
            log.info("Sale registered: {}", productCode);
        } else {
            log.warn("Product does not exist: {}", productCode);
            result.incrementFailure(String.format("Product does not exist: %s", productCode));
        }
    }

    private void processInventoryUpdate(Sale sale, BatchProcessingResult result) {
        Product product = productRepository.findByCode(sale.getProductCode())
                .orElseThrow(() -> new RecordNotFoundException("Product", sale.getProductCode()));

        if (product.getStock() < sale.getQuantity()) {
            throw new InsufficientStockException(
                    sale.getProductCode(),
                    product.getStock(),
                    sale.getQuantity()
            );
        }

        product.reduceStock(sale.getQuantity());
        productRepository.save(product);
        
        sale.markAsProcessed();
        saleRepository.save(sale);

        result.incrementSuccess();
        result.addProcessedItem(String.format("Updated stock for %s: -%d (new stock: %d)",
                product.getCode(), sale.getQuantity(), product.getStock()));
    }

    private SaleDto toDto(Sale sale) {
        return SaleDto.builder()
                .id(sale.getId())
                .productCode(sale.getProductCode())
                .quantity(sale.getQuantity())
                .createdAt(sale.getCreatedAt())
                .processed(sale.getProcessed())
                .build();
    }
}
