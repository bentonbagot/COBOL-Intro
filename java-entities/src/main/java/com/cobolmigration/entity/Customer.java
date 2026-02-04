package com.cobolmigration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Java entity class representing the COBOL REGISTRO-CLIENTE structure.
 * 
 * Original COBOL structure from Spanish-COBOL/EJEMPLO-INDEXADO.cbl:
 * 01 REGISTRO-CLIENTE.
 *    05 CLAVE-CLIENTE   PIC X(10).
 *    05 NOMBRE-CLIENTE  PIC X(30).
 *    05 TELEFONO        PIC X(15).
 * 
 * Used in indexed file: clientes.idx (clients.idx in English)
 */
@Entity
@Table(name = "clientes")
public class Customer {

    @Id
    @Column(name = "clave", length = 10, nullable = false)
    private String clave;

    @Column(name = "nombre", length = 30)
    private String nombre;

    @Column(name = "telefono", length = 15)
    private String telefono;

    public Customer() {
    }

    public Customer(String clave, String nombre, String telefono) {
        this.clave = clave;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "clave='" + clave + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
