package com.example.alejandro.practica4aadinmobiliariacontentprovider;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alejandro on 02/12/2014.
 */
public class Inmueble implements Comparable<Inmueble>, Serializable {

    private int id;
    private int precio;
    private String localidad, direccion, tipo;
    private Integer subido;

    public Inmueble() {
    }

    public Inmueble(int id, int precio, String localidad, String direccion, String tipo, Integer subido) {
        this.id = id;
        this.precio = precio;
        this.localidad = localidad;
        this.direccion = direccion;
        this.tipo = tipo;
        this.subido = subido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public Integer getSubido() {
        return subido;
    }

    public void setSubido(Integer subido) {
        this.subido = subido;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inmueble inmueble = (Inmueble) o;

        if (id != inmueble.id) return false;
        if (precio != inmueble.precio) return false;
        if (subido != inmueble.subido) return false;
        if (!direccion.equals(inmueble.direccion)) return false;
        if (!localidad.equals(inmueble.localidad)) return false;
        if (!tipo.equals(inmueble.tipo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + precio;
        result = 31 * result + localidad.hashCode();
        result = 31 * result + direccion.hashCode();
        result = 31 * result + tipo.hashCode();
        result = 31 * result + subido;
        return result;
    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "id=" + id +
                ", precio=" + precio +
                ", localidad='" + localidad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }


    @Override
    public int compareTo(Inmueble another) {
        return 0;
    }
}
