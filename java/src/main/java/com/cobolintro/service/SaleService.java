package com.cobolintro.service;

import com.cobolintro.dao.ProductDao;
import com.cobolintro.dao.SaleDao;
import com.cobolintro.model.Sale;

/**
 * Service layer for sale operations.
 * Replaces the cross-file validation logic in excercise3-sells.cbl.
 */
public class SaleService {

    private final ProductDao productDao;
    private final SaleDao saleDao;

    public SaleService(ProductDao productDao, SaleDao saleDao) {
        this.productDao = productDao;
        this.saleDao = saleDao;
    }

    /**
     * Creates a sale after validating that the product exists.
     *
     * @param productCode the code of the product being sold
     * @param quantity    the number of units sold
     * @throws com.cobolintro.exception.RecordNotFoundException if the product does not exist
     */
    public void createSale(String productCode, int quantity) {
        productDao.findByCode(productCode);
        saleDao.write(new Sale(productCode, quantity));
    }

    /**
     * Batch generates sample sales for existing products.
     */
    public void generateTestSales() {
        saleDao.write(new Sale("P0001", 2));
        saleDao.write(new Sale("P0002", 10));
        saleDao.write(new Sale("P0003", 5));
        saleDao.write(new Sale("P0004", 20));
        saleDao.write(new Sale("P0005", 8));
        saleDao.write(new Sale("P0001", 3));
    }

    /**
     * Returns the total number of recorded sales.
     *
     * @return the sale count
     */
    public int getSaleCount() {
        return saleDao.count();
    }
}
