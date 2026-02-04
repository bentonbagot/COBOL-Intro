package com.cobolmigration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Java entity class representing the COBOL VENTAS-RECORD structure.
 * 
 * Original COBOL structure from Spanish-COBOL/excercise3-sells.cbl:
 * 01 VENTAS-RECORD.
 *    05 VENTA-CODIGO    PIC X(05).
 *    05 VENTA-CANTIDAD  PIC 9(05).
 * 
 * Used in sequential file: VENTAS.DAT (SELLS.DAT in English)
 */
@Entity
@Table(name = "ventas")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", length = 5, nullable = false)
    private String codigo;

    @Column(name = "cantidad")
    private Integer cantidad;

    public Sale() {
    }

    public Sale(String codigo, Integer cantidad) {
        this.codigo = codigo;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}
