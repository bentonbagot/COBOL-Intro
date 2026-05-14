package com.cobolintro.app;

import java.sql.SQLException;
import java.util.Scanner;

import com.cobolintro.dao.CustomerDao;
import com.cobolintro.dao.DatabaseManager;
import com.cobolintro.model.Customer;
import com.cobolintro.service.CustomerService;

/**
 * Menu-driven console application for customer management.
 * Replaces EXAMPLE-INDEX.cbl lines 29-53.
 */
public class CustomerApp {

    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            return;
        }

        CustomerDao customerDao = new CustomerDao();
        CustomerService customerService = new CustomerService(customerDao);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.println("=== Customer Management ===");
            System.out.println("A - Add Customer");
            System.out.println("B - Search Customer");
            System.out.println("F - Finish");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().trim();

            switch (option.toUpperCase()) {
                case "A":
                    System.out.print("Enter key (10 chars max): ");
                    String key = scanner.nextLine().trim();
                    System.out.print("Enter name (30 chars max): ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter phone (15 chars max): ");
                    String phone = scanner.nextLine().trim();
                    Customer customer = new Customer(key, name, phone);
                    customerService.addCustomer(customer);
                    break;

                case "B":
                    System.out.print("Enter key to search: ");
                    String searchKey = scanner.nextLine().trim();
                    Customer found = customerService.searchCustomer(searchKey);
                    if (found != null) {
                        System.out.println("Key:   " + found.getKey());
                        System.out.println("Name:  " + found.getName());
                        System.out.println("Phone: " + found.getPhone());
                    }
                    break;

                case "F":
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}
