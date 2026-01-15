package com.cobolmodernization.exception;

public class CustomerNotFoundException extends RuntimeException {

    private final String customerKey;

    public CustomerNotFoundException(String customerKey) {
        super("Customer with key '" + customerKey + "' not found");
        this.customerKey = customerKey;
    }

    public CustomerNotFoundException(String customerKey, Throwable cause) {
        super("Customer with key '" + customerKey + "' not found", cause);
        this.customerKey = customerKey;
    }

    public String getCustomerKey() {
        return customerKey;
    }
}
