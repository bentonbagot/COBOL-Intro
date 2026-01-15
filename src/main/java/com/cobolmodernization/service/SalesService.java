package com.cobolmodernization.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cobolmodernization.entity.SalesRecordEntity;
import com.cobolmodernization.exception.ProductNotFoundException;
import com.cobolmodernization.exception.SalesProcessingException;
import com.cobolmodernization.model.SalesRecord;
import com.cobolmodernization.repository.SalesRecordRepository;

@Service
@Transactional
public class SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesService.class);

    private final SalesRecordRepository salesRecordRepository;
    private final ProductService productService;

    public SalesService(SalesRecordRepository salesRecordRepository, ProductService productService) {
        this.salesRecordRepository = salesRecordRepository;
        this.productService = productService;
    }

    public SalesRecordEntity createSale(SalesRecord sale) {
        logger.info("Creating sale for product: {}", sale.getProductCode());

        if (!productService.existsByCode(sale.getProductCode())) {
            logger.error("Product does not exist: {}", sale.getProductCode());
            throw new ProductNotFoundException(sale.getProductCode());
        }

        SalesRecordEntity entity = new SalesRecordEntity(sale.getProductCode(), sale.getQuantity());
        SalesRecordEntity savedSale = salesRecordRepository.save(entity);
        logger.info("Sale registered: product={}, quantity={}", sale.getProductCode(), sale.getQuantity());
        return savedSale;
    }

    public List<SalesRecordEntity> processSalesTransactions(List<SalesRecord> sales) {
        logger.info("Processing {} sales transactions", sales.size());
        List<SalesRecordEntity> processedSales = new ArrayList<>();
        int successCount = 0;

        for (SalesRecord sale : sales) {
            try {
                SalesRecordEntity savedSale = createSale(sale);
                processedSales.add(savedSale);
                successCount++;
            } catch (ProductNotFoundException e) {
                logger.warn("Skipping sale for non-existent product: {}", sale.getProductCode());
            } catch (Exception e) {
                logger.error("Error processing sale for product {}: {}", sale.getProductCode(), e.getMessage());
                throw new SalesProcessingException("Failed to process sale for product: " + sale.getProductCode(), e);
            }
        }

        logger.info("Total sales processed: {}", successCount);
        return processedSales;
    }

    public void updateInventoryFromSales(List<SalesRecord> sales) {
        logger.info("Updating inventory from {} sales", sales.size());

        for (SalesRecord sale : sales) {
            try {
                productService.decreaseStock(sale.getProductCode(), sale.getQuantity());
                logger.info("Inventory updated for product {}: decreased by {}", 
                        sale.getProductCode(), sale.getQuantity());
            } catch (ProductNotFoundException e) {
                logger.warn("Cannot update inventory - product not found: {}", sale.getProductCode());
            } catch (Exception e) {
                logger.error("Error updating inventory for product {}: {}", sale.getProductCode(), e.getMessage());
                throw new SalesProcessingException("Failed to update inventory for product: " + sale.getProductCode(), e);
            }
        }

        logger.info("Inventory update completed");
    }

    public void processAndUpdateInventory(List<SalesRecord> sales) {
        logger.info("Processing sales and updating inventory for {} transactions", sales.size());
        processSalesTransactions(sales);
        updateInventoryFromSales(sales);
        logger.info("Sales processing and inventory update completed");
    }

    @Transactional(readOnly = true)
    public List<SalesRecordEntity> getAllSales() {
        logger.debug("Retrieving all sales records");
        return salesRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SalesRecordEntity> getSalesByProductCode(String productCode) {
        logger.debug("Retrieving sales for product: {}", productCode);
        return salesRecordRepository.findByProductCode(productCode);
    }

    public List<SalesRecordEntity> generateDefaultSales() {
        logger.info("Generating default sales transactions");

        List<SalesRecord> defaultSales = new ArrayList<>();
        defaultSales.add(SalesRecord.builder().productCode("00001").quantity(5).build());
        defaultSales.add(SalesRecord.builder().productCode("00002").quantity(10).build());
        defaultSales.add(SalesRecord.builder().productCode("00003").quantity(3).build());
        defaultSales.add(SalesRecord.builder().productCode("00001").quantity(2).build());
        defaultSales.add(SalesRecord.builder().productCode("00002").quantity(1).build());

        return processSalesTransactions(defaultSales);
    }

    public long countSales() {
        return salesRecordRepository.count();
    }

    @Transactional(readOnly = true)
    public List<SalesRecord> generateSalesReport() {
        logger.info("Generating sales report");
        return salesRecordRepository.findAll().stream()
                .map(entity -> SalesRecord.builder()
                        .productCode(entity.getProductCode())
                        .quantity(entity.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
