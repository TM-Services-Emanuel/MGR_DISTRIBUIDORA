package com.example.fact_express.Modelos;

import java.io.Serializable;

public class Clientes implements Serializable {
    public int idcliente;
    public String cod_int;
    public String nombre_f;
    public String razon_social;
    public String ruc;
    public String direccion;
    public String ref1;
    public String ref2;
    public String celular;
    public int idciudad;
    public String ciudad;

    public Clientes(int idcliente, String cod_int, String nombre_f, String razon_social, String ruc, String direccion, String ref1, String ref2, String celular, String ciudad) {
        this.idcliente = idcliente;
        this.cod_int = cod_int;
        this.nombre_f = nombre_f;
        this.razon_social = razon_social;
        this.ruc = ruc;
        this.direccion = direccion;
        this.ref1 = ref1;
        this.ref2 = ref2;
        this.celular = celular;
        this.ciudad = ciudad;
    }

    public Clientes() {

    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public String getCod_int() {
        return cod_int;
    }

    public void setCod_int(String cod_int) {
        this.cod_int = cod_int;
    }

    public String getNombre_f() {
        return nombre_f;
    }

    public void setNombre_f(String nombre_f) {
        this.nombre_f = nombre_f;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRef1() {
        return ref1;
    }

    public void setRef1(String ref1) {
        this.ref1 = ref1;
    }

    public String getRef2() {
        return ref2;
    }

    public void setRef2(String ref2) {
        this.ref2 = ref2;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
