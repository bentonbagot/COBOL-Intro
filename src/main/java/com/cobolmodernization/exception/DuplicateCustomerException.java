package com.cobolmodernization.exception;

public class DuplicateCustomerException extends RuntimeException {

    private final String customerKey;

    public DuplicateCustomerException(String customerKey) {
        super("Customer with key '" + customerKey + "' already exists");
        this.customerKey = customerKey;
    }

    public DuplicateCustomerException(String customerKey, Throwable cause) {
        super("Customer with key '" + customerKey + "' already exists", cause);
        this.customerKey = customerKey;
    }

    public String getCustomerKey() {
        return customerKey;
    }
}
