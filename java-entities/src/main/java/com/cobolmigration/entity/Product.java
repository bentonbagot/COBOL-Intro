package com.cobolmigration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Java entity class representing the COBOL PRODUCTOS-RECORD structure.
 * 
 * Original COBOL structure from Spanish-COBOL/Excercise1.cbl:
 * 01 PRODUCTOS-RECORD.
 *    05 PROD-CODIGO  PIC X(5).
 *    05 PROD-NOMBRE  PIC X(20).
 *    05 PROD-PRECIO  PIC 9(7)V99.
 *    05 PROD-STOCK   PIC 9(5).
 * 
 * Used in indexed file: PRODUCTOS.DAT
 */
@Entity
@Table(name = "productos")
public class Product {

    @Id
    @Column(name = "codigo", length = 5, nullable = false)
    private String codigo;

    @Column(name = "nombre", length = 20)
    private String nombre;

    @Column(name = "precio", precision = 9, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock")
    private Integer stock;

    public Product() {
    }

    public Product(String codigo, String nombre, BigDecimal precio, Integer stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                '}';
    }
}
