package com.cobolintro.app;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.ProductDao;
import com.cobolintro.dao.SaleDao;
import com.cobolintro.exception.RecordNotFoundException;
import com.cobolintro.service.SaleService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Interactive console application for registering sales.
 * Replaces the main flow of excercise3-sells.cbl.
 */
public class SalesApp {

    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        ProductDao productDao = new ProductDao();
        SaleDao saleDao = new SaleDao();
        SaleService saleService = new SaleService(productDao, saleDao);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean continueSales = true;

            while (continueSales) {
                System.out.print("Enter product code: ");
                String productCode = scanner.nextLine().trim();

                int quantity;
                System.out.print("Enter quantity: ");
                try {
                    quantity = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Sale not registered.");
                    continue;
                }

                try {
                    saleService.createSale(productCode, quantity);
                    System.out.println("Sale registered successfully.");
                } catch (RecordNotFoundException e) {
                    System.out.println("Product not found.");
                }

                System.out.println("Total sales registered so far: " + saleService.getSaleCount());

                System.out.print("Register another sale? (Y/N): ");
                String answer = scanner.nextLine().trim();
                if (answer.equalsIgnoreCase("N")) {
                    continueSales = false;
                }
            }

            System.out.println("Total sales registered: " + saleService.getSaleCount());
            System.out.println("Thank you for using the Sales System. Goodbye!");
        }
    }
}
