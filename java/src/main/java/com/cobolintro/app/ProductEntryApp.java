package com.cobolintro.app;

import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.dao.ProductDao;
import com.cobolintro.exception.DuplicateKeyException;
import com.cobolintro.model.Product;
import com.cobolintro.service.ProductService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Interactive console application for entering products.
 * Replaces the main flow of createdat.cbl.
 */
public class ProductEntryApp {

    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        ProductDao productDao = new ProductDao();
        ProductService productService = new ProductService(productDao);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean continueEntry = true;

            while (continueEntry) {
                System.out.print("Enter product code (5 chars max): ");
                String code = scanner.nextLine().trim();
                if (code.length() > 5) {
                    code = code.substring(0, 5);
                }

                System.out.print("Enter product name (20 chars max): ");
                String name = scanner.nextLine().trim();
                if (name.length() > 20) {
                    name = name.substring(0, 20);
                }

                BigDecimal price;
                System.out.print("Enter price: ");
                try {
                    price = new BigDecimal(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format. Product not saved.");
                    continue;
                }

                int stock;
                System.out.print("Enter stock quantity: ");
                try {
                    stock = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid stock quantity. Product not saved.");
                    continue;
                }

                Product product = new Product(code, name, price, stock);
                try {
                    productService.createProduct(product);
                    System.out.println("Product created successfully: " + product);
                } catch (DuplicateKeyException e) {
                    System.out.println("Error: A product with code '" + code + "' already exists.");
                }

                System.out.print("Do you want to enter another product? (Y/N): ");
                String answer = scanner.nextLine().trim();
                if (answer.equalsIgnoreCase("N")) {
                    continueEntry = false;
                }
            }

            System.out.println("Thank you for using the Product Entry System. Goodbye!");
        }
    }
}
