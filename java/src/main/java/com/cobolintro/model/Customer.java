package com.cobolintro.model;

/**
 * Customer model based on COBOL CLIENTS-RECORD.
 * PIC X(10) key, PIC X(30) name, PIC X(15) phone.
 */
public class Customer {

    private String key;
    private String name;
    private String phone;

    public Customer() {
    }

    public Customer(String key, String name, String phone) {
        this.key = key;
        this.name = name;
        this.phone = phone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
