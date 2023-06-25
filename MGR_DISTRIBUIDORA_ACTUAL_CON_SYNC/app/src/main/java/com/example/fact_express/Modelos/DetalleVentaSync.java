package com.example.fact_express.Modelos;

import java.io.Serializable;

public class DetalleVentaSync implements Serializable {
    public int idventa;
    public int idemision;
    public int productos_idproductos;
    public String cantidad;
    public int precio;
    public int total;
    public int impuesto_aplicado;
    public String um;
    public String promo;

    public DetalleVentaSync(int idventa, int idemision, int productos_idproductos, String cantidad, int precio, int total, int impuesto_aplicado, String um, String promo) {
        this.idventa = idventa;
        this.idemision = idemision;
        this.productos_idproductos = productos_idproductos;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
        this.impuesto_aplicado = impuesto_aplicado;
        this.um = um;
        this.promo = promo;
    }

    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public int getIdemision() {
        return idemision;
    }

    public void setIdemision(int idemision) {
        this.idemision = idemision;
    }

    public int getProductos_idproductos() {
        return productos_idproductos;
    }

    public void setProductos_idproductos(int productos_idproductos) {
        this.productos_idproductos = productos_idproductos;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getImpuesto_aplicado() {
        return impuesto_aplicado;
    }

    public void setImpuesto_aplicado(int impuesto_aplicado) {
        this.impuesto_aplicado = impuesto_aplicado;
    }

    public String getUm() {
        return um;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }
}